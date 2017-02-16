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

import io.sledge.core.api.SledgeConstants;
import io.sledge.core.api.installer.InstallationException;
import io.sledge.core.api.models.ApplicationPackageModel;
import io.sledge.core.api.models.ApplicationPackageState;
import io.sledge.core.api.repository.PackageRepository;
import io.sledge.core.impl.installer.SledgeInstallationManager;
import io.sledge.core.impl.installer.SledgePackageConfigurer;
import io.sledge.core.impl.repository.SledgePackageRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.post.SlingPostConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author oliver.burkhalter
 */
@SlingServlet(
        selectors = "install",
        resourceTypes = "sledge/package",
        methods = "POST")
@Properties({
        @Property(name = "service.description",
                value = "Sledge: Handles installation of application packages",
                propertyPrivate = false),
        @Property(name = "service.vendor",
                value = "Unic AG - Sledge",
                propertyPrivate = false)
})
public class SledgeInstallServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -8845139979153006842L;
    private static final Logger LOG = LoggerFactory.getLogger(SledgeInstallServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String envName = request.getParameter(SledgeConstants.ENVIRONMENT_NAME_PARAM);
        String overwriteEnvFileContent = request.getParameter(SledgeConstants.ENVIRONMENT_FILE_CONTENT_PARAM);

        if (StringUtils.isBlank(envName)) {
            envName = System.getenv(SledgeConstants.ENVIRONMENT_NAME_VARIABLE);
        }

        Resource packageResource = request.getResource();
        ApplicationPackageModel appPackage = packageResource.adaptTo(ApplicationPackageModel.class);
        ResourceResolver resourceResolver = request.getResourceResolver();
        PackageRepository packageRepository = new SledgePackageRepository(resourceResolver);

        try {
            SledgeInstallationManager installationManager = new SledgeInstallationManager(resourceResolver, packageRepository, request.adaptTo(SledgePackageConfigurer.class));
            installationManager.install(appPackage, overwriteEnvFileContent, envName);

        } catch (InstallationException e) {
            LOG.warn("Could not install Sledge package. ", e);
            appPackage.setState(ApplicationPackageState.FAILED);
            packageRepository.updateApplicationPackage(appPackage);
        }

        String redirectUrl = request.getParameter(SlingPostConstants.RP_REDIRECT_TO);
        response.sendRedirect(redirectUrl);
    }
}
