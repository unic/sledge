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