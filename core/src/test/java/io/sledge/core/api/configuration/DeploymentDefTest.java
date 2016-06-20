package io.sledge.core.api.configuration;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author oliver.burkhalter
 */
public class DeploymentDefTest {
	@Test
	public void getPackageNames() throws Exception {
		// Given:
		DeploymentDef testee = new DeploymentDef();

		testee.addPackage(new PackageElement(false, "package1"));
		testee.addPackage(new PackageElement(false, "package2"));
		testee.addPackage(new PackageElement(true, "package3"));

		// When:
		List<String> packageNames = testee.getPackageNames();

		// Then:
		assertThat(packageNames).contains("package1", "package2", "package3");
	}

	@Test
	public void getPackagesForConfiguration() throws Exception {
		// Given:
		DeploymentDef testee = new DeploymentDef();

		testee.addPackage(new PackageElement(false, "package1"));
		testee.addPackage(new PackageElement(false, "package2"));
		testee.addPackage(new PackageElement(true, "package3"));
		testee.addPackage(new PackageElement(true, "package4"));

		// When:
		List<String> packagesForConfiguration = testee.getPackageNamesForConfiguration();

		// Then:
		assertThat(packagesForConfiguration).hasSize(2);
		assertThat(packagesForConfiguration).contains("package3", "package4");
	}
}