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

import java.io.InputStream;

/**
 * @author oliver.burkhalter
 */
public interface DeploymentConfigurationReader {

	/**
	 * @param deployConfigFile The input stream of the sledgefile.xml deployment configuration file. This is should exist in the application package root level.
	 * @return Returns an new object of {@link DeploymentConfiguration} or throws an execption if something goes wrong.
	 * @exception DuplicateEnvironmentException If there are any duplicated environment names in the config file.
	 */
	DeploymentConfiguration parseDeploymentConfiguration(InputStream deployConfigFile);
}
