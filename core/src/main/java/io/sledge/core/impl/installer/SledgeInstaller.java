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
import io.sledge.core.api.installer.ConfigurableInstaller;
import io.sledge.core.api.installer.InstallationException;
import io.sledge.core.api.installer.PackageConfigurer;
import io.sledge.core.api.models.ApplicationPackageType;
import io.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import org.apache.jackrabbit.JcrConstants;
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
 * The {@link SledgeInstaller} handles the installation of an Application package.
 * It loads the sledgefile configuration for the given environment, merges the current configuration with the given configuration
 * and installs the appropriate packages.
 */
public class SledgeInstaller implements ConfigurableInstaller {

	private static final Logger LOG = LoggerFactory.getLogger(SledgeInstaller.class);

	private final PackageConfigurer packageConfigurer;
	private ResourceResolver resourceResolver;
	private String environmentName;
	private Properties propertiesForMerge;

	public SledgeInstaller(ResourceResolver resourceResolver, PackageConfigurer packageConfigurer) {
		if (resourceResolver == null) {
			throw new IllegalArgumentException("Cannot initialize SledgeInstallerImpl because the resourceResolver is null.");
		}
		if (packageConfigurer == null) {
			throw new IllegalArgumentException("Cannot initialize SledgeInstallerImpl because the packageConfigurer is null.");
		}

		this.packageConfigurer = packageConfigurer;
		this.resourceResolver = resourceResolver;
	}

	@Override
	public void install(ApplicationPackage appPackage) throws InstallationException {
		Resource defaultInstallLocationResource = resourceResolver.getResource(SledgeConstants.SLEDGE_INSTALL_LOCATION);
		ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();

		DeploymentConfiguration deploymentConfiguration = appPackageExtractor.getDeploymentConfiguration(appPackage.getPackageFileStream());
		final DeploymentDef deploymentDef = deploymentConfiguration.getDeploymentDefByEnvironment(environmentName);
		final Map<String, Integer> startLevelsByPackageName = deploymentDef.getStartLevelsByPackageName();

		// Load and merge environment properties
		String envFileContent = appPackageExtractor.getEnvironmentFile(environmentName, appPackage);
		Properties envProps = packageConfigurer.mergeProperties(envFileContent, propertiesForMerge);

		// Install the packages
		List<Map.Entry<String, InputStream>> packages = appPackageExtractor.getPackagesByEnvironment(appPackage, environmentName);
		for (Map.Entry<String, InputStream> packageEntry : packages) {
			try {

				configurePackage(deploymentDef, packageConfigurer, envProps, packageEntry);

				Resource installLocationResource = defaultInstallLocationResource;
				if (startLevelsByPackageName.containsKey(packageEntry.getKey())) {
					Integer startLevel = startLevelsByPackageName.get(packageEntry.getKey());
					LOG.info("Setting start level: " + startLevel + " for the package: " + packageEntry.getKey());
					Map<String, Object> props = new HashMap<>();
					props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FOLDER);
					installLocationResource = resourceResolver
							.getResource(defaultInstallLocationResource.getPath() + "/" + String.valueOf(startLevel));
					if (installLocationResource == null) {
						installLocationResource = resourceResolver
								.create(defaultInstallLocationResource, String.valueOf(startLevel), props);
					}
				}

				Map<String, Object> props = new HashMap<>();
				props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);

				String packageResourcePath = installLocationResource.getPath() + "/" + packageEntry.getKey();
				if (!packageResourceExists(packageResourcePath)) {
					Resource fileResource = resourceResolver.create(installLocationResource, packageEntry.getKey(), props);

					props = new HashMap<>();
					props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
					props.put(JcrConstants.JCR_DATA, packageEntry.getValue());
					resourceResolver.create(fileResource, JcrConstants.JCR_CONTENT, props);

					resourceResolver.commit();
				} else {
					LOG.warn("Package: " + packageEntry.getKey()
							+ " has not been installed because it exists already. If you want to update your application, please uninstall first. ");
				}
			} catch (PersistenceException e) {
				throw new InstallationException("Could not install package: " + packageEntry.getKey(), e);
			}
		}
	}

	@Override
	public boolean handles(ApplicationPackage appPackage) {
		return appPackage.getApplicationPackageType().equals(ApplicationPackageType.sledgepackage);
	}

	private void configurePackage(DeploymentDef deploymentDef, PackageConfigurer packageConfigurer, Properties envProps,
			Map.Entry<String, InputStream> packageEntry) {
		final List<String> packageNamesForConfiguration = deploymentDef.getPackageNamesForConfiguration();
		if (packageNamesForConfiguration.contains(packageEntry.getKey())) {
			LOG.info("Configuring package: " + packageEntry.getKey());
			packageEntry.setValue(packageConfigurer.configure(packageEntry.getValue(), packageEntry.getKey(), envProps));
		}
	}

	private boolean packageResourceExists(String packageResourcePath) {
		return resourceResolver.getResource(packageResourcePath) != null;
	}

	@Override
	public void setEnvironmentName(String envName) {
		this.environmentName = envName;
	}

	@Override
	public void setPropertiesForMerge(Properties propsForMerge) {
		this.propertiesForMerge = propsForMerge;
	}
}
