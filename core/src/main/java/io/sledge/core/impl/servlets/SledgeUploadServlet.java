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

import io.sledge.core.api.models.ApplicationPackageModel;
import io.sledge.core.api.models.ApplicationPackageState;
import io.sledge.core.api.models.ApplicationPackageType;
import io.sledge.core.api.repository.PackageRepository;
import io.sledge.core.impl.repository.SledgePackageRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.post.SlingPostConstants;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author oliver.burkhalter
 */
@SlingServlet(
        selectors = "upload",
        resourceTypes = "sledge/app",
        methods = "POST")
@Properties({
        @Property(name = "service.description",
                value = "Sledge: Handles application package uploads"
        ),
        @Property(name = "service.vendor",
                value = "Unic AG - Sledge"
        )
})
public class SledgeUploadServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -8845139979153006842L;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        RequestParameter packageRequestParam = request.getRequestParameter("package");

        if (packageRequestParam != null && !packageRequestParam.isFormField() && packageRequestParam.getInputStream() != null
                && StringUtils.isNoneBlank(packageRequestParam.getFileName())) {

            String fileName = packageRequestParam.getFileName();
            String groupId = request.getRequestParameter("groupId").getString();
            String artifactId = request.getRequestParameter("artifactId").getString();
            String version = request.getRequestParameter("version").getString();

            ApplicationPackageModel appPackage = new ApplicationPackageModel(fileName, groupId, artifactId, version);

            appPackage.setState(ApplicationPackageState.UPLOADED);
            appPackage.setPackageFile(packageRequestParam.getInputStream());
            appPackage.setApplicationPackageType(ApplicationPackageType.sledgepackage);

            PackageRepository packageRepository = new SledgePackageRepository(request.getResourceResolver());
            packageRepository.addOrUpdate(appPackage);

            String redirectUrl = request.getParameter(SlingPostConstants.RP_REDIRECT_TO);
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getRequestURI());
        }
    }
}
