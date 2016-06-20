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
import io.sledge.core.api.extractor.ApplicationPackageExtractor;
import io.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import io.sledge.core.api.models.ApplicationPackage;
import org.apache.commons.io.Charsets;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author oliver.burkhalter
 */
@SlingServlet(selectors = "config",
		resourceTypes = "sledge/package",
		methods = "GET")
@Properties({
		@Property(name = "service.description",
				value = "Sledge: Returns the content of requested config files in an application package",
				propertyPrivate = false),
		@Property(name = "service.vendor",
				value = "Unic AG - Sledge",
				propertyPrivate = false)
})
public class ConfigFileServlet extends SlingAllMethodsServlet {

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		RequestParameter configFileParam = request.getRequestParameter(SledgeConstants.ENVIRONMENT_NAME_PARAM);
		Resource packageResource = request.getResource();

		if(configFileParam != null) {
			String envFilename = configFileParam.getString();

			ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();

			ApplicationPackage appPackage = packageResource.adaptTo(ApplicationPackage.class);
			String fileContent = appPackageExtractor.getEnvironmentFile(envFilename, appPackage);

			response.setCharacterEncoding(Charsets.UTF_8.toString());
			response.getWriter().write(fileContent);
		}
	}
}
