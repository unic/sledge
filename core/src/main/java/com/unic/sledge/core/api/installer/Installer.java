package com.unic.sledge.core.api.installer;

import com.unic.sledge.core.api.models.ApplicationPackage;

/**
 * The Installer takes an {@link com.unic.sledge.core.api.models.ApplicationPackage} and installs it.
 *
 * @author oliver.burkhalter
 */
public interface Installer {

	/**
	 * Installs a given package for a given environment.
	 * May throw an {@link InstallationException} if anything fails during installation process.
	 *
	 * @param appPackage The application package.
	 * @param envName The name of the installing environment.
	 */
	void install(ApplicationPackage appPackage, String envName) throws InstallationException;
}
