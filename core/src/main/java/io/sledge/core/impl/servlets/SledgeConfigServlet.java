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

import com.google.gson.Gson;
import io.sledge.core.api.SledgeConstants;
import io.sledge.core.api.configuration.DeploymentConfiguration;
import io.sledge.core.api.configuration.DeploymentDef;
import io.sledge.core.api.extractor.ApplicationPackageExtractor;
import io.sledge.core.api.installer.PackageConfigurer;
import io.sledge.core.api.models.ApplicationPackageModel;
import io.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import io.sledge.core.impl.installer.SledgePackageConfigurer;
import org.apache.commons.io.Charsets;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

@SlingServlet(selectors = "config",
		resourceTypes = "sledge/package",
		methods = { "GET", "POST" })
@Properties({
		@Property(name = "service.description",
				value = "Sledge: Handles configuration of the app packages"
		),
		@Property(name = "service.vendor",
				value = "Unic AG - Sledge"
		)
})
public class SledgeConfigServlet extends SlingAllMethodsServlet {

	private class ConfigurationDetails {

		private List<String> configurablePackageNames;
		private String configurationProperties;

		public ConfigurationDetails(List<String> configurablePackageNames, String configurationProperties) {
			this.configurablePackageNames = configurablePackageNames;
			this.configurationProperties = configurationProperties;
		}

		public List<String> getConfigurablePackageNames() {
			return configurablePackageNames;
		}

		public void setConfigurablePackageNames(List<String> configurablePackageNames) {
			this.configurablePackageNames = configurablePackageNames;
		}

		public String getConfigurationProperties() {
			return configurationProperties;
		}

		public void setConfigurationProperties(String configurationProperties) {
			this.configurationProperties = configurationProperties;
		}
	}

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		RequestParameter envNameParam = request.getRequestParameter(SledgeConstants.ENVIRONMENT_NAME_PARAM);
		Resource packageResource = request.getResource();

		if (envNameParam != null) {
			String envName = envNameParam.getString();

			ApplicationPackageModel appPackage = packageResource.adaptTo(ApplicationPackageModel.class);
			ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();
			DeploymentConfiguration deploymentConfiguration = appPackageExtractor
					.getDeploymentConfiguration(appPackage.getPackageFileStream());

			final DeploymentDef deploymentDef = deploymentConfiguration.getDeploymentDefByEnvironment(envName);
			final List<String> packageNamesForConfiguration = deploymentDef.getPackageNamesForConfiguration();

			String fileContent = appPackageExtractor.getEnvironmentFile(envName, appPackage);

			String output = new Gson().toJson(new ConfigurationDetails(packageNamesForConfiguration, fileContent));

			response.setCharacterEncoding(Charsets.UTF_8.toString());
			response.getWriter().write(output);
		}
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		String envName = request.getParameter(SledgeConstants.ENVIRONMENT_NAME_PARAM);
		String overwriteEnvFileContent = request.getParameter(SledgeConstants.ENVIRONMENT_FILE_CONTENT_PARAM);
		Resource packageResource = request.getResource();
		ApplicationPackageModel appPackage = packageResource.adaptTo(ApplicationPackageModel.class);

		java.util.Properties overwriteEnvProps = new java.util.Properties();
		if (overwriteEnvFileContent != null) {
			overwriteEnvProps.load(new StringReader(overwriteEnvFileContent));
		}

		ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();
		String envFileContent = appPackageExtractor.getEnvironmentFile(envName, appPackage);

		PackageConfigurer packageConfigurer = request.adaptTo(SledgePackageConfigurer.class);
		java.util.Properties envProps = packageConfigurer.mergeProperties(envFileContent, overwriteEnvProps);
		packageConfigurer.configure(appPackage.getPackageFileStream(), appPackage.getPackageFilename(), envProps);

		// TODO: create configuration preview and save it as a metadata in the application package subnode
	}
}
