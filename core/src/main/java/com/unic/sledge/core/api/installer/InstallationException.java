package com.unic.sledge.core.api.installer;

/**
 * Exception thrown for installation errors.
 *
 * @author oliver.burkhalter
 */
public class InstallationException extends RuntimeException {
	public InstallationException(String message, Exception e) {
		super(message, e);
	}
}
