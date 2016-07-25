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

import io.sledge.core.api.SledgeConstants;
import io.sledge.core.api.configuration.DeploymentConfiguration;
import io.sledge.core.api.configuration.DeploymentDef;
import io.sledge.core.api.extractor.ApplicationPackageExtractor;
import io.sledge.core.api.installer.InstallationException;
import io.sledge.core.api.installer.Installer;
import io.sledge.core.api.installer.PackageConfigurer;
import io.sledge.core.api.models.ApplicationPackage;
import io.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author oliver.burkhalter
 */
public class SledgeInstallerImpl implements Installer {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private SlingHttpServletRequest request;
	private ResourceResolver resourceResolver;

	public SledgeInstallerImpl(SlingHttpServletRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Cannot initialize SledgeInstallerImpl because the request is null.");
		}

		this.request = request;
		this.resourceResolver = request.getResourceResolver();
	}

	@Override
	public void install(ApplicationPackage appPackage, String envName, Properties propsForMerge) throws InstallationException {
		Resource defaultInstallLocationResource = resourceResolver.getResource(SledgeConstants.SLEDGE_INSTALL_LOCATION);
		ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();

		PackageConfigurer packageConfigurer = request.adaptTo(SledgePackageConfigurer.class);
		DeploymentConfiguration deploymentConfiguration = appPackageExtractor.getDeploymentConfiguration(appPackage.getPackageFile());
		final DeploymentDef deploymentDef = deploymentConfiguration.getDeploymentDefByEnvironment(envName);
		final List<String> packageNamesForConfiguration = deploymentDef.getPackageNamesForConfiguration();
		final Map<String, Integer> startLevelsByPackageName = deploymentDef.getStartLevelsByPackageName();

		// Load and merge environment properties
		String envFileContent = appPackageExtractor.getEnvironmentFile(envName, appPackage);
		Properties envProps = packageConfigurer.mergeProperties(envFileContent, propsForMerge);

		// Install the packages
		List<Map.Entry<String, InputStream>> packages = appPackageExtractor.getPackagesByEnvironment(appPackage, envName);
		for (Map.Entry<String, InputStream> packageEntry : packages) {
			try {

				if (packageNamesForConfiguration.contains(packageEntry.getKey())) {
					log.info("Configuring package: " + packageEntry.getKey());
					packageEntry.setValue(packageConfigurer.configure(packageEntry.getValue(), packageEntry.getKey(), envProps));
				}

				Resource installLocationResource = defaultInstallLocationResource;
				if (startLevelsByPackageName.containsKey(packageEntry.getKey())) {
					Integer startLevel = startLevelsByPackageName.get(packageEntry.getKey());
					log.info("Setting start level: " + startLevel + " for the package: " + packageEntry.getKey());
					Map<String, Object> props = new HashMap<>();
					props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FOLDER);
					installLocationResource = resourceResolver.getResource(defaultInstallLocationResource.getPath() + "/" + String.valueOf(startLevel));
					if (installLocationResource == null) {
						installLocationResource = resourceResolver.create(defaultInstallLocationResource, String.valueOf(startLevel), props);
					}
				}

				Map<String, Object> props = new HashMap<>();
				props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);
				Resource fileResource = resourceResolver.create(installLocationResource, packageEntry.getKey(), props);

				props = new HashMap<>();
				props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
				props.put(JcrConstants.JCR_DATA, packageEntry.getValue());
				resourceResolver.create(fileResource, JcrConstants.JCR_CONTENT, props);

				resourceResolver.commit();
			} catch (PersistenceException e) {
				throw new InstallationException("Could not install package: " + packageEntry.getKey(), e);
			}
		}
	}
}
