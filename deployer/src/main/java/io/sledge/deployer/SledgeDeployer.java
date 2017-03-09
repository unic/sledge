/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sledge.deployer;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import groovy.json.JsonException;
import groovy.json.JsonSlurper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Provides common functions to handle automated deployment to a Sling/AEM instance with Sledge installed.
 */
public class SledgeDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(SledgeDeployer.class);

    private static final String SLEDGE_PACKAGES_REDIRECT_URL = "/etc/sledge/packages.html";

    private String targetHost;
    private String targetHostUser;
    private String targetHostPassword;

    public static final String SLEDGE_BASE_PATH = "/etc/sledge/packages";
    public static final String SLEDGE_INSTALL_PATH = "/apps/sledge_packages/install";
    public static final String SLEDGE_PACKAGES_SEARCH_URL = "/etc/sledge/packages.search.json";

    /**
     * Constructs a SledgeDeployer object with the given settings.
     *
     * @param targetHost         The target host (inclusive a port if needed) where to install the Sledge application, e.g. http://localhost:4502
     * @param targetHostUser     A valid admin user
     * @param targetHostPassword The appropriate user password
     */
    public SledgeDeployer(String targetHost, String targetHostUser, String targetHostPassword) {
        if (isEmpty(targetHost) || isEmpty(targetHostUser) || isEmpty(targetHostPassword)) {
            throw new IllegalArgumentException("Please provide a proper targetHost, user and password");
        }

        this.targetHost = targetHost;
        this.targetHostUser = targetHostUser;
        this.targetHostPassword = targetHostPassword;
    }

    public HttpResponse<String> uploadApp(File packageFile) throws UnirestException {
        String uploadUrl = targetHost + SLEDGE_BASE_PATH + ".upload.html";

        LOG.info("Uploading application to: " + uploadUrl);
        return Unirest.post(uploadUrl)
                .basicAuth(targetHostUser, this.targetHostPassword)
                .field("_charset_", "UTF-8")
                .field(":redirect", SLEDGE_PACKAGES_REDIRECT_URL)
                .field("package", packageFile)
                .asString();
    }

    public HttpResponse<String> uploadApp(File packageFile, String groupId, String artifactId, String version) throws UnirestException {
        String uploadUrl = targetHost + SLEDGE_BASE_PATH + ".upload.html";

        LOG.info("Uploading application (" + groupId + ", " + artifactId + ", " + version + ") to: " + uploadUrl);
        return Unirest.post(uploadUrl)
                .basicAuth(targetHostUser, this.targetHostPassword)
                .field("_charset_", "UTF-8")
                .field(":redirect", SLEDGE_PACKAGES_REDIRECT_URL)
                .field("package", packageFile)
                .field("groupId", groupId)
                .field("artifactId", artifactId)
                .field("version", version)
                .asString();
    }

    public HttpResponse<String> installApp(String appPackageName, String environment, String environmentConfigContent)
            throws UnirestException {
        String installUrl = targetHost + SLEDGE_BASE_PATH + "/" + appPackageName + ".install.html";

        LOG.info("Installing application: " + installUrl);
        return Unirest.post(installUrl)
                .basicAuth(targetHostUser, targetHostPassword)
                .field("_charset_", "UTF-8")
                .field(":redirect", SLEDGE_PACKAGES_REDIRECT_URL)
                .field("environmentName", environment)
                .field("environmentFileContent", environmentConfigContent)
                .asString();
    }

    public HttpResponse<String> uninstallApp(String appPackageName) throws UnirestException {
        String uninstallUrl = targetHost + SLEDGE_BASE_PATH + "/" + appPackageName + ".uninstall.html";

        LOG.info("Uninstalling application: " + uninstallUrl);
        return Unirest.post(uninstallUrl)
                .basicAuth(targetHostUser, targetHostPassword)
                .field(":redirect", SLEDGE_PACKAGES_REDIRECT_URL)
                .asString();
    }

    public HttpResponse<String> uninstallApp(String groupId, String artifactId, String version) throws UnirestException {
        String uninstallUrl = targetHost + SLEDGE_BASE_PATH + ".uninstall.html";

        LOG.info("Uninstalling multiple application packages at url: " + uninstallUrl);
        LOG.info("Uninstalling applications with following coordinates: groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version);
        return Unirest.post(uninstallUrl)
                .basicAuth(targetHostUser, targetHostPassword)
                .field(":redirect", SLEDGE_PACKAGES_REDIRECT_URL)
                .field("groupId", groupId)
                .field("artifactId", artifactId)
                .field("version", version)
                .asString();
    }

    public HttpResponse<String> removeApp(String appPackageName) throws UnirestException {
        String removeUrl = targetHost + SLEDGE_BASE_PATH + "/" + appPackageName;

        LOG.info("Removing application: " + removeUrl);
        return Unirest.post(removeUrl)
                .basicAuth(targetHostUser, targetHostPassword)
                .field(":redirect", SLEDGE_PACKAGES_REDIRECT_URL)
                .field(":operation", "delete")
                .asString();
    }

    public HttpResponse<String> removeApp(String groupId, String artifactId, String version) throws UnirestException {
        String removeUrl = targetHost + SLEDGE_BASE_PATH + ".remove.html";

        LOG.info("Removing multiple applications at url: " + removeUrl);
        LOG.info("Removing applications with following coordinates: groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version);

        return Unirest.post(removeUrl)
                .basicAuth(targetHostUser, targetHostPassword)
                .field(":redirect", SLEDGE_PACKAGES_REDIRECT_URL)
                .field("groupId", groupId)
                .field("artifactId", artifactId)
                .field("version", version)
                .asString();
    }

    public HttpResponse<String> removeResource(String resourcePath) throws UnirestException {
        String removeUrl = targetHost + resourcePath;

        LOG.info("Removing resource: " + removeUrl);
        return Unirest.post(removeUrl)
                .basicAuth(targetHostUser, targetHostPassword)
                .field(":redirect", SLEDGE_PACKAGES_REDIRECT_URL)
                .field(":operation", "delete")
                .asString();
    }

    public List<HashMap> searchPackages(String groupId, String artifactId, String version) throws UnirestException {
        List<HashMap> packageList;
        JsonSlurper jsonSlurper = new JsonSlurper();

        String url = targetHost + SLEDGE_PACKAGES_SEARCH_URL;
        HttpResponse<String> response = Unirest.get(url)
                .basicAuth(targetHostUser, targetHostPassword)
                .queryString("groupId", groupId)
                .queryString("artifactId", artifactId)
                .queryString("version", version)
                .asString();

        Map packagesObject = (Map) jsonSlurper.parseText(response.getBody());
        packageList = (ArrayList<HashMap>) packagesObject.get("packages");

        return packageList;
    }

    public HttpResponse<String> checkSledgeStatus() throws UnirestException {
        String sledgeCheckUrl = targetHost + SLEDGE_BASE_PATH + ".html";

        return Unirest.get(sledgeCheckUrl)
                .basicAuth(targetHostUser, targetHostPassword)
                .asString();
    }

    public BundlesStatusResponse checkActiveBundles() throws UnirestException {
        JsonSlurper jsonSlurper = new JsonSlurper();

        // Felix Bundles json url
        String bundlesJsonUrl = targetHost + "/system/console/bundles.json";

        int bundlesResolved = 1;
        int bundlesInstalled = 1;

        HttpResponse<String> bundlesResponse = Unirest.get(bundlesJsonUrl)
                .basicAuth(targetHostUser, targetHostPassword)
                .asString();

        try {
            Map bundlesObject = (Map) jsonSlurper.parseText(bundlesResponse.getBody());

            // status result array: (bundles existing, active, fragment, resolved, installed)
            ArrayList<Integer> bundlesStatusItems = (ArrayList<Integer>) bundlesObject.get("s");
            bundlesResolved = bundlesStatusItems.get(3);
            bundlesInstalled = bundlesStatusItems.get(4);

        } catch (JsonException ex) {
            LOG.debug("Calling bundles state failed with a JsonException, retrying...\n");
        }

        return new BundlesStatusResponse(bundlesResolved, bundlesInstalled);
    }

    private class BundlesStatusResponse {
        private final int resolved;
        private final int installed;

        public BundlesStatusResponse(int bundlesResolved, int bundlesInstalled) {
            this.resolved = bundlesResolved;
            this.installed = bundlesInstalled;
        }

        public int getResolved() {
            return resolved;
        }

        public int getInstalled() {
            return installed;
        }
    }
}
