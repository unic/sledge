package com.unic.sledge.core.impl.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.post.SlingPostConstants;

import com.unic.sledge.core.api.ApplicationPackage;
import com.unic.sledge.core.api.repository.PackageRepository;

/**
 * @author oliver.burkhalter
 */
@SlingServlet(
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

    @Reference
    private PackageRepository packageRepository;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        RequestParameter packageRequestParam = request.getRequestParameter("package");

        if (packageRequestParam != null && !packageRequestParam.isFormField()) {
            String fileName = packageRequestParam.getFileName();

            ApplicationPackage appPackage = new ApplicationPackage(fileName);
            
            // TODO: Extract artifactId and groupId from package
            appPackage.setArtifactId("com.sledge.unknown");
            appPackage.setGroupId("sledge.group");
            
            appPackage.setPackageFile(packageRequestParam.getInputStream());

            packageRepository.addApplicationPackage(appPackage);

            String redirectUrl = request.getParameter(SlingPostConstants.RP_REDIRECT_TO);
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getRequestURI() + ".html");
        }
    }
}
