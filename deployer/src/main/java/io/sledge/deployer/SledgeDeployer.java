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
import groovy.json.JsonSlurper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Thread.sleep;
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

    public void checkActiveBundles(int checkCount, int checkWaitTimeInMilliseconds) throws UnirestException, InterruptedException {
        JsonSlurper jsonSlurper = new JsonSlurper();

        // Felix Bundles json url
        String bundlesJsonUrl = this.targetHost + "/system/console/bundles.json";

        int bundlesResolved = 0;
        int bundlesInstalled = 0;

        LOG.info("Checking bundles state...");
        for (int i = 1; i <= checkCount; i++) {

            HttpResponse<String> bundlesResponse = Unirest.get(bundlesJsonUrl).
                    basicAuth(this.targetHostUser, this.targetHostPassword)
                    .asString();

            Map bundlesObject = (Map) jsonSlurper.parseText(bundlesResponse.getBody());

            // status result array: (bundles existing, active, fragment, resolved, installed)
            ArrayList<Integer> bundlesStatusItems = (ArrayList<Integer>) bundlesObject.get("s");
            bundlesResolved = bundlesStatusItems.get(3);
            bundlesInstalled = bundlesStatusItems.get(4);

            LOG.info("Check " + i + "/" + checkCount + " - " + bundlesObject.get("status"));

            sleep(checkWaitTimeInMilliseconds);
        }

        if (bundlesInstalled > 0) {
            LOG.info("******");
            LOG.info("ERROR: There are " + bundlesInstalled + " bundles in INSTALLED state.");
            LOG.info("******");
        }
        if (bundlesResolved > 0) {
            LOG.info("******");
            LOG.info("ERROR: There are " + bundlesResolved + " bundles in RESOLVED state.");
            LOG.info("******");
        }
        if (bundlesResolved > 0 || bundlesInstalled > 0) {
            LOG.info("Please check Felix console for more details and restart instance if needed...");
            LOG.info("------");
        }
    }
}
