package com.unic.sledge.core.impl.configuration;

import com.unic.sledge.core.api.configuration.DeploymentConfiguration;
import com.unic.sledge.core.api.configuration.DeploymentDef;
import com.unic.sledge.core.api.configuration.DuplicateEnvironmentException;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author oliver.burkhalter
 */
public class DeploymentConfigurationReaderXmlTest {

	private File sledgeConfigurationFile = new File("src/test/resources/sledgefile-example.xml");
	private File sledgeConfigFileWithDuplicateEnv = new File("src/test/resources/sledgefile-example-duplicateEnvironment.xml");

	@Test
	public void testParseDeploymentConfiguration() throws Exception {
		// Given:
		DeploymentConfigurationReaderXml testee = new DeploymentConfigurationReaderXml();

		// When:
		DeploymentConfiguration deploymentConfiguration = testee.parseDeploymentConfiguration(new FileInputStream(sledgeConfigurationFile));

		// Then:
		assertThat(deploymentConfiguration.getDeploymentDefinitions()).hasSize(3);

		// test-author, test-publish environment configuration
		DeploymentDef deploymentDefTest = deploymentConfiguration.getDeploymentDefinitions().get(0);

		assertThat(deploymentDefTest.getEnvironments()).containsOnly("test-author", "test-publish");
		assertThat(deploymentDefTest.getInstallerPackageNames())
				.contains("com.my.package.common-1.0.0-crx.zip", "com.my.package.templating-1.0.0-crx.zip",
						"com.my.package.ws-1.0.0-crx.zip");
		assertThat(deploymentDefTest.getConfigurerPackageNames()).contains("com.my.package.configuration-1.0.0-crx.zip");

		// prod-author environment configuration
		DeploymentDef deploymentDefProdAuthor = deploymentConfiguration.getDeploymentDefinitions().get(1);
		assertThat(deploymentDefProdAuthor.getEnvironments()).containsOnly("prod-author");
		assertThat(deploymentDefProdAuthor.getInstallerPackageNames())
				.contains("com.my.package.common-1.0.0-crx.zip", "com.my.package.templating-1.0.0-crx.zip",
						"com.my.package.ws-1.0.0-crx.zip", "com.my.package.migration-1.0.0-crx.zip");
		assertThat(deploymentDefProdAuthor.getConfigurerPackageNames()).contains("com.my.package.configuration-1.0.0-crx.zip");

		// prod-publish environment configuration
		DeploymentDef deploymentDefProdPublish = deploymentConfiguration.getDeploymentDefinitions().get(2);
		assertThat(deploymentDefProdPublish.getEnvironments()).containsOnly("prod-publish");
		assertThat(deploymentDefProdPublish.getInstallerPackageNames())
				.contains("com.my.package.common-1.0.0-crx.zip", "com.my.package.templating-1.0.0-crx.zip",
						"com.my.package.ws-1.0.0-crx.zip");
		assertThat(deploymentDefProdPublish.getConfigurerPackageNames()).contains("com.my.package.configuration-1.0.0-crx.zip");
	}

	@Test(expected = DuplicateEnvironmentException.class)
	public void testParseDeploymentConfigurationWithDuplicateEnvironment() throws Exception {
		// Given:
		DeploymentConfigurationReaderXml testee = new DeploymentConfigurationReaderXml();

		// When, Then:
		DeploymentConfiguration deploymentConfiguration = testee
				.parseDeploymentConfiguration(new FileInputStream(sledgeConfigFileWithDuplicateEnv));
	}
}