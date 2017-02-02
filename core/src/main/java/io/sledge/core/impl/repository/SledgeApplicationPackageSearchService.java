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

package io.sledge.core.impl.repository;

import io.sledge.core.api.repository.ApplicationPackageSearchService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.join;

public class SledgeApplicationPackageSearchService implements ApplicationPackageSearchService {

    private static final String QUERY_BASE = "/jcr:root/etc/sledge/packages//*[@sling:resourceType='sledge/package' and %s]";
    private static final String GROUPID_PREDICATE = "@groupId='%s'";
    private static final String ARTIFACTID_PREDICATE = "@artifactId='%s'";
    private static final String VERSION_PREDICATE = "@version='%s'";

    private final ResourceResolver resourceResolver;

    public SledgeApplicationPackageSearchService(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    @Override
    public <T> List<T> find(String groupId, String artifactId, String version, Class<T> adaptableType) {
        List<Resource> appPackageResources = find(groupId, artifactId, version);
        return appPackageResources.stream().map(resource -> resource.adaptTo(adaptableType)).collect(Collectors.toList());
    }

    @Override
    public List<Resource> find(String groupId, String artifactId, String version) {
        List<Resource> applicationPackageResources = new ArrayList<>();
        List<String> predicates = new ArrayList<>();

        if (isNotEmpty(groupId)) {
            predicates.add(String.format(GROUPID_PREDICATE, groupId));
        }
        if (isNotEmpty(artifactId)) {
            predicates.add(String.format(ARTIFACTID_PREDICATE, artifactId));
        }
        if (isNotEmpty(version)) {
            predicates.add(String.format(VERSION_PREDICATE, version));
        }

        String predicatesAndString = join(predicates, " and ");
        final String query = String.format(QUERY_BASE, predicatesAndString);
        Iterator<Resource> result = resourceResolver.findResources(query, "xpath");

        result.forEachRemaining(resource -> {
            applicationPackageResources.add(resource);
        });

        return applicationPackageResources;
    }
}
