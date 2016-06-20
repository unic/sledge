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

package io.sledge.core.api;

/**
 * Common Sledge constants.
 * 
 * @author oliver.burkhalter
 */
public class SledgeConstants {

    /**
     * sledge/package resource type.
     */
    public static final String RT_PACKAGE = "sledge/package";

	/**
	 * The path where application packages gets extracted and installed.
     */
    public static final String SLEDGE_INSTALL_LOCATION = "/apps/sledge_packages/install";
	public static final String SLEDGEFILE_XML = "sledgefile.xml";

	public static final String ENVIRONMENT_NAME_PARAM = "environmentName";
	public static final String ENVIRONMENT_FILE_CONTENT_PARAM = "environmentFileContent";
}
