package com.unic.sledge.core.impl.servlets;

import com.unic.sledge.core.api.SledgeConstants;
import com.unic.sledge.core.api.installer.Installer;
import com.unic.sledge.core.api.models.ApplicationPackage;
import com.unic.sledge.core.api.models.ApplicationPackageState;
import com.unic.sledge.core.api.repository.PackageRepository;
import com.unic.sledge.core.impl.installer.SledgeInstallerImpl;
import com.unic.sledge.core.impl.repository.SledgePackageRepository;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.post.SlingPostConstants;

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

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		String envName = request.getParameter(SledgeConstants.ENVIRONMENT_NAME_PARAM);
		// String envFileContent = request.getParameter("environmentFileContent");

		Resource packageResource = request.getResource();
		ApplicationPackage appPackage = packageResource.adaptTo(ApplicationPackage.class);

		Installer installer = new SledgeInstallerImpl(request.getResourceResolver());
		installer.install(appPackage, envName);

		PackageRepository packageRepository = new SledgePackageRepository(request.getResourceResolver());
		appPackage.setState(ApplicationPackageState.INSTALLED.toString());
		packageRepository.updateApplicationPackage(appPackage);

		String redirectUrl = request.getParameter(SlingPostConstants.RP_REDIRECT_TO);
		response.sendRedirect(redirectUrl);
	}
}
