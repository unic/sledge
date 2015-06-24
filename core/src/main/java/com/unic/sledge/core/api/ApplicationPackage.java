package com.unic.sledge.core.api;

import java.io.InputStream;

/**
 * An <i>ApplicationPackage</i> provides all needed information for the whole
 * application. Its defined by a file name, an artifactId and a groupId.
 * <p>
 * It contains the bundles or packages (e.g. CRX packages), configurations and the deployment descriptor file.
 * 
 * @author oliver.burkhalter
 */
public class ApplicationPackage {

    private String artifactId;
    private String groupId;
    private String packageFilename;
    private InputStream packageFile;

    public ApplicationPackage(String packageFileName) {
        this.packageFilename = packageFileName;
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
}
