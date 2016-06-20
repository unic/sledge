package io.sledge.core.impl.servlets;

import io.sledge.core.api.models.ApplicationPackage;
import io.sledge.core.api.models.ApplicationPackageState;
import io.sledge.core.api.repository.PackageRepository;
import io.sledge.core.impl.repository.SledgePackageRepository;
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
				value = "Sledge: Handles application package uploads",
				propertyPrivate = false),
		@Property(name = "service.vendor",
				value = "Unic AG - Sledge",
				propertyPrivate = false)
})
public class SledgeUploadServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = -8845139979153006842L;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		RequestParameter packageRequestParam = request.getRequestParameter("package");

		if (packageRequestParam != null && !packageRequestParam.isFormField() && packageRequestParam.getInputStream() != null) {
			String fileName = packageRequestParam.getFileName();

			ApplicationPackage appPackage = new ApplicationPackage(fileName);

			appPackage.setState(ApplicationPackageState.UPLOADED.toString());
			appPackage.setPackageFile(packageRequestParam.getInputStream());

			PackageRepository packageRepository = new SledgePackageRepository(request.getResourceResolver());
			packageRepository.addApplicationPackage(appPackage);

			String redirectUrl = request.getParameter(SlingPostConstants.RP_REDIRECT_TO);
			response.sendRedirect(redirectUrl);
		} else {
			response.sendRedirect(request.getRequestURI());
		}
	}
}
