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
import io.sledge.core.api.models.ApplicationPackageModel;
import io.sledge.core.impl.repository.SledgeApplicationPackageSearchService;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;


@SlingServlet(
        selectors = "search",
        resourceTypes = "sledge/app",
        extensions = "json",
        methods = "GET")
@Properties({
        @Property(name = "service.description",
                value = "Sledge: Searches Application packages by groupId, artifactId or version"),
        @Property(name = "service.vendor",
                value = "Unic AG - Sledge")
})
public class SledgePackageSearchServlet extends SlingSafeMethodsServlet {

    private static String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        String groupId = isBlank(request.getParameter("groupId")) ? "" : request.getParameter("groupId");
        String artifactId = isBlank(request.getParameter("artifactId")) ? "" : request.getParameter("artifactId");
        String version = isBlank(request.getParameter("version")) ? "" : request.getParameter("version");

        SledgeApplicationPackageSearchService packageSearchService = new SledgeApplicationPackageSearchService(request.getResourceResolver());

        List<ApplicationPackageModel> applicationPackages = packageSearchService.find(groupId, artifactId, version, ApplicationPackageModel.class);

        JSONObject jsonObj = new JSONObject();
        JSONArray appPackageArray = new JSONArray();

        try {
            for (ApplicationPackage appPackage : applicationPackages) {
                JSONObject appObj = new JSONObject();

                appObj.put("packageFilename", appPackage.getPackageFilename());
                appObj.put("groupId", appPackage.getGroupId());
                appObj.put("artifactId", appPackage.getArtifactId());
                appObj.put("version", appPackage.getVersion());

                appPackageArray.put(appObj);
            }

            jsonObj.put("packages", appPackageArray);

        } catch (JSONException e) {
            throw new RuntimeException("Could not generate Application package search result json.", e);
        }

        response.setContentType(JSON_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8.toString());
        response.getWriter().write(jsonObj.toString());
    }
}
