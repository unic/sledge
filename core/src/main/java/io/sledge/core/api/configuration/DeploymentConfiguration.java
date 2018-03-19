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

import java.util.List;
import java.util.Optional;

/**
 * The DeploymentConfiguration contains all the deployment definition elements.
 *
 * You can load a deployment definition for a specific environment which provides you with the needed packages for installation for
 * this environment.
 */
public class DeploymentConfiguration {

	private List<DeploymentDef> deploymentDefinitions;

	public DeploymentConfiguration(List<DeploymentDef> deploymentDefinitions) {
		this.deploymentDefinitions = deploymentDefinitions;
	}

	public List<DeploymentDef> getDeploymentDefinitions() {
		return deploymentDefinitions;
	}

	public DeploymentDef getDeploymentDefByEnvironment(String environnment) {
		Optional<DeploymentDef> deploymentDefOptional = deploymentDefinitions
				.stream()
				.filter(d -> d.getEnvironments().contains(environnment))
				.findFirst();

		return deploymentDefOptional.orElseThrow(
				() -> new EnvironmentNotFoundException("Could not find environment in deployment configuration: " + environnment));
	}
}
