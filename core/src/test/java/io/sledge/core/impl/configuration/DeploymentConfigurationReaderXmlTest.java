package io.sledge.core.impl.configuration;

import io.sledge.core.api.configuration.DeploymentConfiguration;
import io.sledge.core.api.configuration.DeploymentDef;
import io.sledge.core.api.configuration.DuplicateEnvironmentException;
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
		assertThat(deploymentDefTest.getPackages()).extracting("packageName")
				.contains("com.my.package.common-1.0.0-crx.zip", "com.my.package.templating-1.0.0-crx.zip",
						"com.my.package.ws-1.0.0-crx.zip", "com.my.package.configuration-1.0.0-crx.zip");
		assertThat(deploymentDefTest.getPackages()).filteredOn("configure", true).hasSize(1);

		// prod-author environment configuration
		DeploymentDef deploymentDefProdAuthor = deploymentConfiguration.getDeploymentDefinitions().get(1);
		assertThat(deploymentDefProdAuthor.getEnvironments()).containsOnly("prod-author");
		assertThat(deploymentDefProdAuthor.getPackages()).extracting("packageName")
				.contains("com.my.package.common-1.0.0-crx.zip", "com.my.package.templating-1.0.0-crx.zip",
						"com.my.package.ws-1.0.0-crx.zip", "com.my.package.migration-1.0.0-crx.zip",
						"com.my.package.configuration-1.0.0-crx.zip");
		assertThat(deploymentDefProdAuthor.getPackages()).filteredOn("configure", true).hasSize(1);

		// prod-publish environment configuration
		DeploymentDef deploymentDefProdPublish = deploymentConfiguration.getDeploymentDefinitions().get(2);
		assertThat(deploymentDefProdPublish.getEnvironments()).containsOnly("prod-publish");
		assertThat(deploymentDefProdPublish.getPackages()).extracting("packageName")
				.contains("com.my.package.common-1.0.0-crx.zip", "com.my.package.templating-1.0.0-crx.zip",
						"com.my.package.ws-1.0.0-crx.zip", "com.my.package.configuration-1.0.0-crx.zip");
		assertThat(deploymentDefProdPublish.getPackages()).filteredOn("configure", true).hasSize(1);
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