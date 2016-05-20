package com.unic.sledge.core.api.installer;

import com.unic.sledge.core.api.models.ApplicationPackage;

/**
 * The Installer takes an {@link com.unic.sledge.core.api.models.ApplicationPackage} and installs it.
 *
 * @author oliver.burkhalter
 */
public interface Installer {

	/**
	 * Installs a given package. May throw an {@link InstallationException} if anything fails during installation process.
	 *
	 * @param appPackage The application package.
	 */
	void install(ApplicationPackage appPackage) throws InstallationException;
}
