package io.sledge.core.api.configuration;

/**
 * @author oliver.burkhalter
 */
public class DeploymentConfigurationReaderException extends RuntimeException {

	public DeploymentConfigurationReaderException(String message, Exception e) {
		super(message, e);
	}
}
