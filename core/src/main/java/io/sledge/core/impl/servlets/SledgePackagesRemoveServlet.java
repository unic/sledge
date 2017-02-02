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

import io.sledge.core.api.repository.ApplicationPackageSearchService;
import io.sledge.core.impl.repository.SledgeApplicationPackageSearchService;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * Removes multiple packages from the Sledge repository by given artifactId, groupId or/and version.
 */
@SlingServlet(
        selectors = "remove",
        resourceTypes = "sledge/app",
        methods = "POST")
@Properties({
        @Property(name = "service.description", value = "Sledge: Removes multiple packages from Sledge repository at once."),
        @Property(name = "service.vendor", value = "Unic AG - Sledge")
})
public class SledgePackagesRemoveServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SledgePackagesRemoveServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        RequestParameter groupIdReqParam = request.getRequestParameter("groupId");
        RequestParameter artifactIdReqParam = request.getRequestParameter("artifactId");
        RequestParameter versionReqParam = request.getRequestParameter("version");

        String groupId = groupIdReqParam == null ? "" : groupIdReqParam.getString();
        String artifactId = artifactIdReqParam == null ? "" : artifactIdReqParam.getString();
        String version = versionReqParam == null ? "" : versionReqParam.getString();

        ResourceResolver resourceResolver = request.getResourceResolver();
        ApplicationPackageSearchService appPackageSearchService = new SledgeApplicationPackageSearchService(resourceResolver);
        List<Resource> applicationPackages = appPackageSearchService.find(groupId, artifactId, version);

        for (Resource appPackageResource : applicationPackages) {
            LOG.info("Removing..." + appPackageResource.getPath());

            resourceResolver.delete(appPackageResource);
            resourceResolver.commit();

            LOG.info("Removed package: " + appPackageResource.getPath());
        }

        String redirectUrl = request.getRequestURI();
        response.sendRedirect(redirectUrl);
    }
}
