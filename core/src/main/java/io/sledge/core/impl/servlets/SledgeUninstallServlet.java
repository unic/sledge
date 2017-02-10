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

import io.sledge.core.api.installer.UninstallationException;
import io.sledge.core.api.models.ApplicationPackageModel;
import io.sledge.core.api.repository.PackageRepository;
import io.sledge.core.impl.installer.SledgeInstallationManager;
import io.sledge.core.impl.installer.SledgePackageConfigurer;
import io.sledge.core.impl.repository.SledgePackageRepository;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.post.SlingPostConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;

@SlingServlet(
        selectors = "uninstall",
        resourceTypes = "sledge/package",
        methods = "POST")
@Properties({
        @Property(name = "service.description",
                value = "Sledge: Handles uninstallation of application packages",
                propertyPrivate = false),
        @Property(name = "service.vendor",
                value = "Unic AG - Sledge",
                propertyPrivate = false)
})
public class SledgeUninstallServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SledgeUninstallServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ApplicationPackageModel appPackage = request.getResource().adaptTo(ApplicationPackageModel.class);

        String redirectUrl = request.getParameter(SlingPostConstants.RP_REDIRECT_TO);

        try {
            ResourceResolver resourceResolver = request.getResourceResolver();
            PackageRepository packageRepository = new SledgePackageRepository(resourceResolver);
            SledgeInstallationManager installationManager = new SledgeInstallationManager(resourceResolver, packageRepository, request.adaptTo(SledgePackageConfigurer.class));
            installationManager.uninstall(appPackage);

        } catch (UninstallationException e) {
            LOG.error("Failed uninstalling Application package. ", e);
            redirectUrl += "?error=failed";
        }

        response.sendRedirect(redirectUrl);
    }
}
