package com.unic.sledge.core.api.configuration;

/**
 * Represents a &lt;package> element in the sledgefile.xml config file.
 * It simply defines the package name and if this package should be environment configured.
 *
 * @author oliver.burkhalter
 */
public class PackageElement {

	private final boolean configure;
	private final String packageName;

	public PackageElement(boolean configure, String packageName) {
		this.configure = configure;
		this.packageName = packageName;
	}

	public boolean isConfigure() {
		return configure;
	}

	public String getPackageName() {
		return packageName;
	}
}
