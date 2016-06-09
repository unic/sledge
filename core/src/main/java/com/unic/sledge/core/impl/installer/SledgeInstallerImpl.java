package com.unic.sledge.core.impl.installer;

import com.unic.sledge.core.api.SledgeConstants;
import com.unic.sledge.core.api.configuration.DeploymentConfiguration;
import com.unic.sledge.core.api.configuration.DeploymentDef;
import com.unic.sledge.core.api.extractor.ApplicationPackageExtractor;
import com.unic.sledge.core.api.installer.InstallationException;
import com.unic.sledge.core.api.installer.Installer;
import com.unic.sledge.core.api.installer.PackageConfigurer;
import com.unic.sledge.core.api.models.ApplicationPackage;
import com.unic.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
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
		Resource installLocationResource = resourceResolver.getResource(SledgeConstants.SLEDGE_INSTALL_LOCATION);
		ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();

		PackageConfigurer packageConfigurer = request.adaptTo(SledgePackageConfigurer.class);
		DeploymentConfiguration deploymentConfiguration = appPackageExtractor.getDeploymentConfiguration(appPackage.getPackageFile());
		final DeploymentDef deploymentDef = deploymentConfiguration.getDeploymentDefByEnvironment(envName);
		final List<String> packageNamesForConfiguration = deploymentDef.getPackageNamesForConfiguration();

		// Load and merge environment properties
		String envFileContent = appPackageExtractor.getEnvironmentFile(envName, appPackage);
		Properties envProps = mergeProperties(envFileContent, propsForMerge);

		// Install the packages
		List<Map.Entry<String, InputStream>> packages = appPackageExtractor.getPackagesByEnvironment(appPackage, envName);
		for (Map.Entry<String, InputStream> packageEntry : packages) {
			try {

				if (packageNamesForConfiguration.contains(packageEntry.getKey())) {
					log.info("Configuring package: " + packageEntry.getKey());
					packageEntry.setValue(packageConfigurer.configure(packageEntry.getValue(), packageEntry.getKey(), envProps));
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

	private Properties mergeProperties(String envFileContent, Properties propsForMerge) {
		Properties mergedProps = new Properties();
		try {

			// Support internal property references for application package provided properties
			Properties origProps = new Properties();
			origProps.load(new StringReader(envFileContent));
			String configuredEnvironmentFileContent = StrSubstitutor.replace(envFileContent, origProps);

			mergedProps.load(new StringReader(configuredEnvironmentFileContent));

			// Support internal property references for overwrite properties
			StringWriter propsForMergeWriter = new StringWriter();
			propsForMerge.store(propsForMergeWriter, "");
			String propsForMergeAsString = propsForMergeWriter.getBuffer().toString();
			String configuredPropsForMerge = StrSubstitutor.replace(propsForMergeAsString, propsForMerge);
			Properties reconfiguredPropsForMerge = new Properties();
			reconfiguredPropsForMerge.load(new StringReader(configuredPropsForMerge));

			mergedProps.putAll(reconfiguredPropsForMerge);

		} catch (IOException e) {
			throw new InstallationException("Could not load environment properties.", e);
		}

		return mergedProps;
	}
}
