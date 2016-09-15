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

package io.sledge.core.api.connectors;

import java.util.List;

import io.sledge.core.api.models.ApplicationPackage;

/**
 * @author oliver.burkhalter
 */
public interface PackageSourceConnector {

    /**
     * Loads and converts Application package artifacts from the remote source.
     * 
     * @return Returns a list of loaded Application packages from the remote source.
     */
    List<ApplicationPackage> loadApplicationPackages();

}
