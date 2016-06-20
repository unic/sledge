package io.sledge.core.api.installer;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Takes a zip/jar package and configures it according to the given environment configuration.
 *
 * @author oliver.burkhalter
 */
public interface PackageConfigurer {

	/**
	 * @param packageStream The current package input stream to configure.
	 * @param packageName The filename of the package.
	 * @param props The Properties object with the needed environment properties or also an empty Properties object is possible.
	 * @return Returns a new environment configured package stream.
	 */
	InputStream configure(InputStream packageStream, String packageName, Properties props);
}
