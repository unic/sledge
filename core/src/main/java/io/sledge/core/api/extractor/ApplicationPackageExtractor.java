package io.sledge.core.api.extractor;

import io.sledge.core.api.configuration.DeploymentConfiguration;
import io.sledge.core.api.models.ApplicationPackage;
import io.sledge.core.api.configuration.DeploymentConfigurationReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * The {@link ApplicationPackageExtractor} provides functionality to get the needed content of an {@link ApplicationPackage} zip file.
 * <p>
 * The {@link DeploymentConfigurationReader} helps to read the {@link DeploymentConfiguration} out
 * of the {@link ApplicationPackage}.
 * </p>
 *
 * @author oliver.burkhalter
 */
public interface ApplicationPackageExtractor {

	/**
	 * Looks up the available environment files in the app package.
	 *
	 * @return A list of file names of the environment files
	 */
	List<String> getEnvironmentNames(ApplicationPackage appPackage);

	/**
	 * Gets the given environment file content as text.
	 *
	 * @param environmentFileName
	 * @return The environment file content as string
	 */
	String getEnvironmentFile(String environmentFileName, ApplicationPackage appPackage);

	/**
	 * Gets all the available packages in the application package as {@link InputStream} objects mapped to their file name.
	 *
	 * @return A map of package input streams mapped to their file name
	 */
	Map<String, InputStream> getPackages(ApplicationPackage appPackage);

	/**
	 * Gets all the packages for the given environment. It reads the sledgefile.xml to determine the packages for a specific environment.
	 *
	 * @param appPackage The provided application package.
	 * @param envName    The name of the environment.
	 * @return A list of map entries of package input streams mapped to their package file name given by the environment.
	 */
	List<Map.Entry<String, InputStream>> getPackagesByEnvironment(ApplicationPackage appPackage, String envName);

	/**
	 * @return Returns the deployment configuration object parsed from the sledgefile.xml contained at the root level in the application package.
	 */
	DeploymentConfiguration getDeploymentConfiguration(InputStream appPackageInputStream);
}
