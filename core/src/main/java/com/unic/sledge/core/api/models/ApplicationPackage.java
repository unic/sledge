package com.unic.sledge.core.api.models;

import com.unic.sledge.core.api.extractor.ApplicationPackageExtractor;
import com.unic.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * An <i>ApplicationPackage</i> provides all needed information for the whole
 * application. Its defined by a file name, an artifactId and a groupId.
 * It contains the bundles or packages (e.g. CRX packages), configurations and the deployment descriptor file.
 *
 * @author oliver.burkhalter
 */
@Model(adaptables = Resource.class)
public class ApplicationPackage {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ValueMapValue
	private String artifactId;

	@ValueMapValue
	private String groupId;

	@ValueMapValue
	private String packageType;

	@ValueMapValue
	private String packageFilename;

	private String path;

	@ChildResource(name = "packageFile/jcr:content")
	private InputStream packageFile;

	@ValueMapValue
	private String state;

	public ApplicationPackage() {
	}

	public ApplicationPackage(String packageFileName) {
		this.packageFilename = packageFileName;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getPackageFilename() {
		return packageFilename;
	}

	public void setPackageFilename(String packageFilename) {
		this.packageFilename = packageFilename;
	}

	public InputStream getPackageFile() {
		return packageFile;
	}

	public void setPackageFile(InputStream packageFile) {
		this.packageFile = packageFile;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getState() {
		return state;
	}

	/**
	 * @param state A defined string from the {@link ApplicationPackageState} enum
	 */
	public void setState(String state) {
		this.state = state;
	}

	public List<String> getEnvironmentFilenames() {
		ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();
		return appPackageExtractor.getEnvironmentFilenames(this);
	}
}
