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

package io.sledge.core.api.installer;

import java.io.InputStream;
import java.util.Properties;

/**
 * Takes a zip/jar package and configures it according to the given environment configuration.
 *
 * @author oliver.burkhalter
 */
public interface PackageConfigurer {

	/**
	 * @param packageStream The current package input stream to configure.
	 * @param packageName The filename of the package.
	 * @param props The Properties object with the needed environment properties or also an empty Properties object is possible.
	 * @return Returns a new environment configured package stream.
	 */
	InputStream configure(InputStream packageStream, String packageName, Properties props);

	/**
	 *
	 * @param envFileContent environment properties
	 * @param propsForMerge initial Properties object
	 * @return merged Properies object
	 */
	Properties mergeProperties(String envFileContent, Properties propsForMerge);
}
