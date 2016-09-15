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

/**
 * Represents a &lt;package> element in the sledgefile.xml config file.
 * It simply defines the package name and if this package should be environment configured.
 *
 * @author oliver.burkhalter
 */
public class PackageElement {

	private final boolean configure;
	private final String packageName;
	private final int startLevel;

	public PackageElement(boolean configure, String packageName) {
		 this(configure, packageName, 0);
	}

	public PackageElement(boolean configure, String packageName, int startLevel) {
		this.configure = configure;
		this.packageName = packageName;
		this.startLevel = startLevel;
	}

	public boolean isConfigure() {
		return configure;
	}

	public String getPackageName() {
		return packageName;
	}

	public int getStartLevel() {
		return startLevel;
	}
}
