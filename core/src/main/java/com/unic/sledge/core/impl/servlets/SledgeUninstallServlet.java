package com.unic.sledge.core.impl.servlets;

import com.unic.sledge.core.api.SledgeConstants;
import com.unic.sledge.core.api.extractor.ApplicationPackageExtractor;
import com.unic.sledge.core.api.models.ApplicationPackageState;
import com.unic.sledge.core.api.repository.PackageRepository;
import com.unic.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import com.unic.sledge.core.api.models.ApplicationPackage;
import com.unic.sledge.core.impl.repository.SledgePackageRepository;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.post.SlingPostConstants;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author oliver.burkhalter
 */
@SlingServlet(
		selectors = "uninstall",
		resourceTypes = "sledge/app",
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

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		RequestParameter packagePath = request.getRequestParameter("packagePath");

		if (packagePath != null) {

			Resource appPackageResource = request.getResourceResolver().getResource(packagePath.getString());
			ApplicationPackage appPackage = appPackageResource.adaptTo(ApplicationPackage.class);

			ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();
			Map<String, InputStream> packages = appPackageExtractor.getPackages(appPackage);

			for (Map.Entry<String, InputStream> packageEntry : packages.entrySet()) {
				ResourceResolver resolver = request.getResourceResolver();
				Resource packageResource = resolver.getResource(SledgeConstants.SLEDGE_INSTALL_LOCATION + "/" + packageEntry.getKey());

				if(packageResource != null) {
					resolver.delete(packageResource);
					resolver.commit();
				}
			}

			PackageRepository packageRepository = new SledgePackageRepository(request.getResourceResolver());
			appPackage.setState(ApplicationPackageState.UNINSTALLED.toString());
			packageRepository.updateApplicationPackage(appPackage);

			String redirectUrl = request.getParameter(SlingPostConstants.RP_REDIRECT_TO);
			response.sendRedirect(redirectUrl);
		} else {
			response.sendRedirect(request.getRequestURI());
		}
	}
}
