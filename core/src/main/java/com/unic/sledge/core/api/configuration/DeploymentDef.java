package com.unic.sledge.core.api.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a &lt;deployment-def> element in the sledgefile.xml config file.
 *
 * @author oliver.burkhalter
 */
public class DeploymentDef {

	private List<String> environments = new ArrayList<>();;

	private List<PackageElement> packages = new ArrayList<>();

	public List<String> getEnvironments() {
		return environments;
	}

	public void setEnvironments(List<String> environments) {
		this.environments = environments;
	}

	public List<PackageElement> getPackages() {
		return packages;
	}

	public List<String> getPackageNames() {
		return packages.stream().map(p -> p.getPackageName()).collect(Collectors.toList());
	}

	public List<String> getPackageNamesForConfiguration() {
		return packages.stream().filter(p -> p.isConfigure()).map(p -> p.getPackageName()).collect(Collectors.toList());
	}

	public void addPackage(PackageElement packageElement) {
		packages.add(packageElement);
	}
}
