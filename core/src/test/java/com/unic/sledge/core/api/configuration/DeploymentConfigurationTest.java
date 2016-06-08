package com.unic.sledge.core.api.configuration;

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