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

import io.sledge.core.api.models.ApplicationPackageState;
import io.sledge.core.api.models.ApplicationPackageType;

import java.io.InputStream;

/**
 * An <i>ApplicationPackage</i> provides all needed information for the whole
 * application. It is identified by Maven-style coordinates: groupId, artifactId and version.
 * It may contain the osgi-bundles or packages (e.g. CRX packages), configurations and the deployment descriptor file <i>sledgefile.xml</i>.
 */
public interface ApplicationPackage {

    String getGroupId();

    String getArtifactId();

    String getVersion();

    ApplicationPackageType getApplicationPackageType();

    String getPackageFilename();

    InputStream getPackageFile();

    ApplicationPackageState getState();

    String getUsedEnvironment();

    void setState(ApplicationPackageState state);

    void setUsedEnvironment(String environmenName);
}
