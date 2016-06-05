package com.unic.sledge.core.impl.servlets;

import com.unic.sledge.core.api.SledgeConstants;
import com.unic.sledge.core.api.extractor.ApplicationPackageExtractor;
import com.unic.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import com.unic.sledge.core.api.models.ApplicationPackage;
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
