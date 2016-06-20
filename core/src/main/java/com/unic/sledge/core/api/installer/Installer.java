package com.unic.sledge.core.api.installer;

import com.unic.sledge.core.api.models.ApplicationPackage;

import java.util.Properties;

/**
 * The Installer takes an {@link com.unic.sledge.core.api.models.ApplicationPackage} and installs it.
 *
 * @author oliver.burkhalter
 */
public interface Installer {

	/**
	 * Installs a given package for a given environment. It takes the environment configuration provided by the given Application package and
	 * merges it with the given {#propsForMerge} properties object.
	 * May throw an {@link InstallationException} if anything fails during installation process.
	 *
	 * @param appPackage    The application package.
	 * @param envName       The name of the installing environment.
	 * @param propsForMerge A properties string which overwrites the provided environment properties from the Application package.
	 */
	void install(ApplicationPackage appPackage, String envName, Properties propsForMerge) throws InstallationException;
}
