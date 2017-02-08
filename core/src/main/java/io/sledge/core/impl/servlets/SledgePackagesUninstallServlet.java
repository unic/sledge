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

package io.sledge.core.impl.servlets;

import io.sledge.core.api.ApplicationPackage;
import io.sledge.core.api.installer.UninstallationException;
import io.sledge.core.api.installer.Uninstaller;
import io.sledge.core.api.models.ApplicationPackageModel;
import io.sledge.core.api.models.ApplicationPackageState;
import io.sledge.core.api.repository.ApplicationPackageSearchService;
import io.sledge.core.api.repository.PackageRepository;
import io.sledge.core.impl.installer.SledgeUninstaller;
import io.sledge.core.impl.repository.SledgeApplicationPackageSearchService;
import io.sledge.core.impl.repository.SledgePackageRepository;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import static io.sledge.core.api.models.ApplicationPackageState.INSTALLED;

/**
 * Provides an uninstallation of multiple packages.
 * This is useful for easy uninstallation of a package without knowing in before what version should be uninstalled.
 * You can uninstall for example all packages with a specific groupId.
 */
@SlingServlet(
        selectors = "uninstall",
        resourceTypes = "sledge/app",
        methods = "POST")
@Properties({
        @Property(name = "service.description", value = "Sledge: Handles uninstallation of mulitple application packages at once."),
        @Property(name = "service.vendor", value = "Unic AG - Sledge")
})
public class SledgePackagesUninstallServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SledgePackagesUninstallServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        RequestParameter groupIdReqParam = request.getRequestParameter("groupId");
        RequestParameter artifactIdReqParam = request.getRequestParameter("artifactId");
        RequestParameter versionReqParam = request.getRequestParameter("version");

        String groupId = groupIdReqParam == null ? "" : groupIdReqParam.getString();
        String artifactId = artifactIdReqParam == null ? "" : artifactIdReqParam.getString();
        String version = versionReqParam == null ? "" : versionReqParam.getString();

        ApplicationPackageSearchService appPackageSearchService = new SledgeApplicationPackageSearchService(request.getResourceResolver());
        List<ApplicationPackageModel> applicationPackages = appPackageSearchService.find(groupId, artifactId, version, ApplicationPackageModel.class);

        PackageRepository packageRepository = new SledgePackageRepository(request.getResourceResolver());
        Uninstaller uninstaller = new SledgeUninstaller(request.getResourceResolver());

        for (ApplicationPackage appPackage : applicationPackages) {
            try {
                // Only uninstall packages with INSTALLED state
                if (INSTALLED.equals(appPackage.getState())) {
                    LOG.info("Uninstalling..." + appPackage.getPackageFilename());
                    uninstaller.uninstall(appPackage);

                    appPackage.setState(ApplicationPackageState.UNINSTALLED);
                    appPackage.setUsedEnvironment("");

                    packageRepository.updateApplicationPackage(appPackage);

                    LOG.info("Uninstalled package: " + appPackage.getPackageFilename());
                }

            } catch (UninstallationException e) {
                LOG.error("Failed uninstalling Application package with name: " + appPackage.getPackageFilename(), e);
            }
        }

        String redirectUrl = request.getRequestURI();
        response.sendRedirect(redirectUrl);
    }
}
