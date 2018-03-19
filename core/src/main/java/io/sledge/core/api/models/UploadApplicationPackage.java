package io.sledge.core.api.models;

import io.sledge.core.api.ApplicationPackage;

import java.io.InputStream;

/**
 * This {@link ApplicationPackage} implementation is mainly used for the uploading new Application packages to the
 * Sledge packages repository.
 * If you have already a Application package resource available then you can use the {@linkplain ApplicationPackageModel}
 *
 * @see ApplicationPackageModel
 */
public class UploadApplicationPackage implements ApplicationPackage {

	private String groupId;
	private String artifactId;
	private String version;
	private ApplicationPackageType applicationPackageType;
	private String packageFilename;
	private ApplicationPackageState state;
	private String usedEnvironment;
	private InputStream packageInputStream;

	public UploadApplicationPackage(String fileName, String groupId, String artifactId, String version, InputStream packageInputStream) {
		this.packageFilename = fileName;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.packageInputStream = packageInputStream;
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public String getArtifactId() {
		return artifactId;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public ApplicationPackageType getApplicationPackageType() {
		return applicationPackageType;
	}

	public void setApplicationPackageType(ApplicationPackageType appPackageType) {
		this.applicationPackageType = appPackageType;
	}

	@Override
	public String getPackageFilename() {
		return packageFilename;
	}

	@Override
	public InputStream getPackageFileStream() {
		return packageInputStream;
	}

	@Override
	public ApplicationPackageState getState() {
		return state;
	}

	@Override
	public String getUsedEnvironment() {
		return usedEnvironment;
	}

	@Override
	public void setState(ApplicationPackageState state) {
		this.state = state;
	}

	@Override
	public void setUsedEnvironment(String environmenName) {
		this.usedEnvironment = environmenName;
	}
}