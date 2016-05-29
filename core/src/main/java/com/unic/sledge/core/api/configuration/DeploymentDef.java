package com.unic.sledge.core.api.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oliver.burkhalter
 */
public class DeploymentDef {

	private List<String> environments;

	private List<String> installerPackageNames = new ArrayList<>();

	private List<String> configurerPackageNames = new ArrayList<>();

	public List<String> getEnvironments() {
		return environments;
	}

	public void setEnvironments(List<String> environments) {
		this.environments = environments;
	}

	public List<String> getInstallerPackageNames() {
		return installerPackageNames;
	}

	public void addInstallerPackageName(String packageName) {
		installerPackageNames.add(packageName);
	}

	public List<String> getConfigurerPackageNames() {
		return configurerPackageNames;
	}

	public void addConfigurerPackageName(String packageName) {
		configurerPackageNames.add(packageName);
	}
}
