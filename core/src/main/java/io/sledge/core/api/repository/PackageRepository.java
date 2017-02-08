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

package io.sledge.core.api.repository;

import io.sledge.core.api.ApplicationPackage;
import org.apache.sling.api.resource.Resource;

import java.util.List;

/**
 * Defines a package repository where the app manages all the loaded {@link ApplicationPackage} resources.
 *
 * @author oliver.burkhalter
 */
public interface PackageRepository {

    List<ApplicationPackage> getApplicationPackages();

    void addApplicationPackage(ApplicationPackage appPackage);

    void updateApplicationPackage(ApplicationPackage appPackage);

    /**
     * @return Returns the root node of the repository of the packages. Mostly /etc/sledge/packages.
     */
    Resource getRepositoryRootResource();

    void addOrUpdate(ApplicationPackage appPackage);
}
