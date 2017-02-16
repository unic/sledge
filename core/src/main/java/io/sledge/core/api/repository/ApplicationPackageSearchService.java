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

import org.apache.sling.api.resource.Resource;

import java.util.List;

/**
 * Finds Application packages in the Sledge repository by the groupId, artifactId or version properties.
 */
public interface ApplicationPackageSearchService {

    /**
     * Returns a list with Application packages found by the given parameters.
     * The parameters are only taken into account if they are not null or empty.
     *
     * @return A list of Application packages with the adaptable type of T or an empty list. The type T must be adaptable to a Sling Resource.
     */
    <T> List<T> find(String groupId, String artifactId, String version, Class<T> adaptableType);

    List<Resource> find(String groupId, String artifactId, String version);
}
