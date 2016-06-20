package io.sledge.core.api.configuration;

import java.io.InputStream;

/**
 * @author oliver.burkhalter
 */
public interface DeploymentConfigurationReader {

	/**
	 * @param deployConfigFile The input stream of the sledgefile.xml deployment configuration file. This is should exist in the application package root level.
	 * @return Returns an new object of {@link DeploymentConfiguration} or throws an execption if something goes wrong.
	 * @exception DuplicateEnvironmentException If there are any duplicated environment names in the config file.
	 */
	DeploymentConfiguration parseDeploymentConfiguration(InputStream deployConfigFile);
}
