package io.sledge.core.api.configuration;

/**
 * @author oliver.burkhalter
 */
public class DuplicateEnvironmentException extends RuntimeException {
	public DuplicateEnvironmentException(String message) {
		super(message);
	}
}
