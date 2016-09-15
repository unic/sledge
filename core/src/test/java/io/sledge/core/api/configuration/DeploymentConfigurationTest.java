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

package io.sledge.core.api.configuration;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @author oliver.burkhalter
 */
public class DeploymentConfigurationTest {

	@Test
	public void getDeploymentDefByEnvironment() throws Exception {
		// Given:
		DeploymentDef ddef1 = new DeploymentDef();
		ddef1.setEnvironments(Arrays.asList("test-author", "test-publish"));
		ddef1.addPackage(new PackageElement(false, "my.package-apps.zip"));
		ddef1.addPackage(new PackageElement(true, "my.package-configuration.zip"));

		DeploymentDef ddef2 = new DeploymentDef();
		ddef2.setEnvironments(Arrays.asList("prod-author"));
		ddef2.addPackage(new PackageElement(false, "my.package-apps.zip"));

		DeploymentConfiguration deploymentConfig = new DeploymentConfiguration(Lists.newArrayList(ddef1, ddef2));

		// When:
		DeploymentDef result = deploymentConfig.getDeploymentDefByEnvironment("test-publish");

		// Then:
		assertThat(result.getEnvironments()).contains("test-author", "test-publish");
		assertThat(result.getPackages()).extracting("packageName", "configure")
				.contains(tuple("my.package-apps.zip", false), tuple("my.package-configuration.zip", true));
	}

	@Test(expected = EnvironmentNotFoundException.class)
	public void getDeploymentDefOfNonExistingEnvironment() throws Exception {
		// Given:
		DeploymentDef ddef1 = new DeploymentDef();
		ddef1.setEnvironments(Arrays.asList("test-author", "test-publish"));
		ddef1.addPackage(new PackageElement(false, "my.package-apps.zip"));
		ddef1.addPackage(new PackageElement(true, "my.package-configuration.zip"));

		DeploymentConfiguration deploymentConfig = new DeploymentConfiguration(Lists.newArrayList(ddef1));

		// When, Then:
		DeploymentDef result = deploymentConfig.getDeploymentDefByEnvironment("prod-publish");
	}
}