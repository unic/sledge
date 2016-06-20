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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a &lt;deployment-def> element in the sledgefile.xml config file.
 *
 * @author oliver.burkhalter
 */
public class DeploymentDef {

	private List<String> environments = new ArrayList<>();;

	private List<PackageElement> packages = new ArrayList<>();

	public List<String> getEnvironments() {
		return environments;
	}

	public void setEnvironments(List<String> environments) {
		this.environments = environments;
	}

	public List<PackageElement> getPackages() {
		return packages;
	}

	public List<String> getPackageNames() {
		return packages.stream().map(p -> p.getPackageName()).collect(Collectors.toList());
	}

	public List<String> getPackageNamesForConfiguration() {
		return packages.stream().filter(p -> p.isConfigure()).map(p -> p.getPackageName()).collect(Collectors.toList());
	}

	public void addPackage(PackageElement packageElement) {
		packages.add(packageElement);
	}
}
