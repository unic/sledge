package com.unic.sledge.core.api.extractor;

import com.unic.sledge.core.api.models.ApplicationPackage;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * The {@link ApplicationPackageExtractor} provides functionality to get the needed content of an {@link ApplicationPackage} zip file.
 *
 * @author oliver.burkhalter
 */
public interface ApplicationPackageExtractor {

	/**
	 * Looks up the available environment files in the app package.
	 *
	 * @return A list of file names of the environment files
	 */
	List<String> getEnvironmentFilenames(ApplicationPackage appPackage);

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
}
