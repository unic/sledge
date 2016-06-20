package io.sledge.core.api.configuration;

/**
 * @author oliver.burkhalter
 */
public class EnvironmentNotFoundException extends RuntimeException {
	public EnvironmentNotFoundException(String msg) {
		super(msg);
	}
}
