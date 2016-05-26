package com.unic.sledge.core.api.configuration;

import java.util.List;
import java.util.Optional;

/**
 * @author oliver.burkhalter
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
