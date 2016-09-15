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
import io.sledge.core.api.configuration.DeploymentConfiguration;
import io.sledge.core.api.configuration.DeploymentDef;
import io.sledge.core.api.extractor.ApplicationPackageExtractor;
import io.sledge.core.api.models.ApplicationPackage;
import io.sledge.core.api.models.ApplicationPackageState;
import io.sledge.core.api.repository.PackageRepository;
import io.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import io.sledge.core.impl.repository.SledgePackageRepository;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
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
		resourceTypes = "sledge/package",
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
		ApplicationPackage appPackage = request.getResource().adaptTo(ApplicationPackage.class);

		ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();
		DeploymentConfiguration deploymentConfiguration = appPackageExtractor.getDeploymentConfiguration(appPackage.getPackageFile());
		final DeploymentDef deploymentDef = deploymentConfiguration.getDeploymentDefByEnvironment(appPackage.getUsedEnvironment());
		final Map<String, Integer> startLevelsByPackageName = deploymentDef.getStartLevelsByPackageName();


		Map<String, InputStream> packages = appPackageExtractor.getPackages(appPackage);

		for (Map.Entry<String, InputStream> packageEntry : packages.entrySet()) {
			ResourceResolver resolver = request.getResourceResolver();
			String packageName = packageEntry.getKey();
			String packagePath = SledgeConstants.SLEDGE_INSTALL_LOCATION + "/" + packageName;
			String startLevelFolder = null;
			if (startLevelsByPackageName.keySet().contains(packageName)) {
				Integer startLevel = startLevelsByPackageName.get(packageName);
				startLevelFolder = SledgeConstants.SLEDGE_INSTALL_LOCATION + "/" + String.valueOf(startLevel);
				packagePath = startLevelFolder + "/" + packageName;
			}
			Resource packageResource = resolver.getResource(packagePath);
			Resource startLevelFolderResource = resolver.getResource(startLevelFolder);

			if (packageResource != null) {
				resolver.delete(packageResource);
			}

			if (startLevelFolderResource != null && !startLevelFolderResource.hasChildren()) {
				resolver.delete(startLevelFolderResource);
			}

			resolver.commit();
		}

		PackageRepository packageRepository = new SledgePackageRepository(request.getResourceResolver());
		appPackage.setState(ApplicationPackageState.UNINSTALLED.toString());
		appPackage.setUsedEnvironment("");
		packageRepository.updateApplicationPackage(appPackage);

		String redirectUrl = request.getParameter(SlingPostConstants.RP_REDIRECT_TO);
		response.sendRedirect(redirectUrl);
	}
}
