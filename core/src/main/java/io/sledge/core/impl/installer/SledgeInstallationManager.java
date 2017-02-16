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
import io.sledge.core.api.installer.ConfigurableInstaller;
import io.sledge.core.api.installer.InstallationException;
import io.sledge.core.api.installer.Installer;
import io.sledge.core.api.installer.PackageConfigurer;
import io.sledge.core.api.installer.Uninstaller;
import io.sledge.core.api.models.ApplicationPackageState;
import io.sledge.core.api.repository.PackageRepository;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Handles the installation or uninstallation of several {@link io.sledge.core.api.installer.Installer} or {@link io.sledge.core.api.installer.Uninstaller}
 * with a given {@link ApplicationPackage}.
 */
public class SledgeInstallationManager {

    private static final Logger LOG = LoggerFactory.getLogger(SledgeInstallationManager.class);

    private final PackageRepository packageRepository;
    private final ResourceResolver resourceResolver;
    private final PackageConfigurer packageConfigurer;

    public SledgeInstallationManager(ResourceResolver resourceResolver, PackageRepository packageRepository, PackageConfigurer packageConfigurer) {
        this.resourceResolver = resourceResolver;
        this.packageRepository = packageRepository;
        this.packageConfigurer = packageConfigurer;
    }

    public void install(ApplicationPackage appPackage, String overwriteEnvFileContent, String envName) throws InstallationException {
        ConfigurableInstaller sledgePackageInstaller = new SledgeInstaller(resourceResolver, packageConfigurer);
        Installer genericInstaller = new GenericInstaller(resourceResolver);

        if (sledgePackageInstaller.handles(appPackage)) {
            handleSledgePackageInstallation(sledgePackageInstaller, appPackage, overwriteEnvFileContent, envName);
            appPackage.setUsedEnvironment(envName);
            appPackage.setState(ApplicationPackageState.INSTALLED);
            packageRepository.updateApplicationPackage(appPackage);

        } else if (genericInstaller.handles(appPackage)) {
            genericInstaller.install(appPackage);
            appPackage.setState(ApplicationPackageState.INSTALLED);
            packageRepository.updateApplicationPackage(appPackage);

        } else {
            LOG.error("No Installer found for installing the provided package.");
            appPackage.setState(ApplicationPackageState.FAILED);
            packageRepository.updateApplicationPackage(appPackage);
        }
    }

    public void uninstall(ApplicationPackage appPackage) {
        Uninstaller sledgeUninstaller = new SledgeUninstaller(resourceResolver);
        Uninstaller genericUninstaller = new GenericUninstaller(resourceResolver);

        if (sledgeUninstaller.handles(appPackage)) {
            sledgeUninstaller.uninstall(appPackage);
            appPackage.setState(ApplicationPackageState.UNINSTALLED);
            appPackage.setUsedEnvironment("");

        } else if (genericUninstaller.handles(appPackage)) {
            genericUninstaller.uninstall(appPackage);
            appPackage.setState(ApplicationPackageState.UNINSTALLED);

        } else {
            LOG.error("No Uninstaller found for installing the provided package.");
            appPackage.setState(ApplicationPackageState.FAILED);
        }

        packageRepository.updateApplicationPackage(appPackage);
    }

    private void handleSledgePackageInstallation(ConfigurableInstaller installer, ApplicationPackage appPackage, String overwriteEnvFileContent, String envName) {
        java.util.Properties overwriteEnvProps = new java.util.Properties();
        if (overwriteEnvFileContent != null) {
            try {
                overwriteEnvProps.load(new StringReader(overwriteEnvFileContent));
            } catch (IOException e) {
                throw new InstallationException("Could not read configurations from properties file input.", e);
            }
        }

        installer.setEnvironmentName(envName);
        installer.setPropertiesForMerge(overwriteEnvProps);
        installer.install(appPackage);
    }
}
