package com.unic.sledge.core.api.configuration;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author oliver.burkhalter
 */
public class DeploymentConfigurationTest {

	@Test
	public void getDeploymentDefByEnvironment() throws Exception {
		// Given:
		DeploymentDef ddef1 = new DeploymentDef();
		ddef1.setEnvironments(Arrays.asList("test-author", "test-publish"));
		ddef1.addConfigurerPackageName("my.package-configuration.zip");
		ddef1.addInstallerPackageName("my.package-apps.zip");

		DeploymentDef ddef2 = new DeploymentDef();
		ddef2.setEnvironments(Arrays.asList("prod-author"));
		ddef2.addConfigurerPackageName("my.package-configuration.zip");
		ddef2.addInstallerPackageName("my.package-apps.zip");

		DeploymentConfiguration deploymentConfig = new DeploymentConfiguration(Lists.newArrayList(ddef1, ddef2));

		// When:
		DeploymentDef result = deploymentConfig.getDeploymentDefByEnvironment("test-publish");

		// Then:
		assertThat(result.getEnvironments()).contains("test-author", "test-publish");
		assertThat(result.getInstallerPackageNames()).contains("my.package-apps.zip");
	}
}