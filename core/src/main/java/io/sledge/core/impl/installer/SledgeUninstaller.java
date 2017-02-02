/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sledge.core.impl.installer;

import io.sledge.core.api.ApplicationPackage;
import io.sledge.core.api.SledgeConstants;
import io.sledge.core.api.configuration.DeploymentConfiguration;
import io.sledge.core.api.configuration.DeploymentDef;
import io.sledge.core.api.extractor.ApplicationPackageExtractor;
import io.sledge.core.api.installer.UninstallationException;
import io.sledge.core.api.installer.Uninstaller;
import io.sledge.core.api.models.ApplicationPackageState;
import io.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.InputStream;
import java.util.Map;

public class SledgeUninstaller implements Uninstaller {

    private final ResourceResolver resourceResolver;

    public SledgeUninstaller(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    @Override
    public void uninstall(ApplicationPackage applicationPackage) {
        ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();
        DeploymentConfiguration deploymentConfiguration = appPackageExtractor.getDeploymentConfiguration(applicationPackage.getPackageFile());
        final DeploymentDef deploymentDef = deploymentConfiguration.getDeploymentDefByEnvironment(applicationPackage.getUsedEnvironment());
        final Map<String, Integer> startLevelsByPackageName = deploymentDef.getStartLevelsByPackageName();

        Map<String, InputStream> packages = appPackageExtractor.getPackages(applicationPackage);

        for (Map.Entry<String, InputStream> packageEntry : packages.entrySet()) {
            String packageName = packageEntry.getKey();
            String packagePath = SledgeConstants.SLEDGE_INSTALL_LOCATION + "/" + packageName;
            String startLevelFolder = null;

            if (startLevelsByPackageName.keySet().contains(packageName)) {
                Integer startLevel = startLevelsByPackageName.get(packageName);
                startLevelFolder = SledgeConstants.SLEDGE_INSTALL_LOCATION + "/" + String.valueOf(startLevel);
                packagePath = startLevelFolder + "/" + packageName;
            }

            Resource packageResource = resourceResolver.getResource(packagePath);
            Resource startLevelFolderResource = resourceResolver.getResource(startLevelFolder);

            try {
                if (packageResource != null) {
                    resourceResolver.delete(packageResource);
                }

                if (startLevelFolderResource != null && !startLevelFolderResource.hasChildren()) {
                    resourceResolver.delete(startLevelFolderResource);
                }

                resourceResolver.commit();

            } catch (PersistenceException ex) {
                throw new UninstallationException("Could not uninstall Application package properly.", ex);
            }
        }

    }
}
