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

import io.sledge.core.api.ApplicationPackage;
import io.sledge.core.api.SledgeConstants;
import io.sledge.core.api.models.ApplicationPackageModel;
import io.sledge.core.api.repository.PackageRepository;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Defines the central package repository of the Sledge app.
 * By default this is: <code>/etc/sledge/packages</code>.
 */
public class SledgePackageRepository implements PackageRepository {

	public static final String APP_PACKAGES_ROOT_PATH = "/etc/sledge/packages";

	ResourceResolver resourceResolver;

	public SledgePackageRepository(ResourceResolver resourceResolver) {
		if (resourceResolver == null) {
			throw new IllegalArgumentException("Cannot initialize PackageRepository because no valid resource resolver is available.");
		}

		this.resourceResolver = resourceResolver;
	}

	@Override
	public List<ApplicationPackage> getApplicationPackages() {
		List<ApplicationPackage> appPackages = new ArrayList<>();

		resourceResolver.refresh();

		Iterator<Resource> appPackagesIter = getRepositoryRootResource().listChildren();

		while (appPackagesIter.hasNext()) {
			Resource appPackageResource = appPackagesIter.next();
			if (isAclPrimaryType(appPackageResource)) {
				continue;
			}
			appPackages.add(createApplicationPackage(appPackageResource));
		}

		return appPackages;
	}

	@Override
	public void addApplicationPackage(ApplicationPackage appPackage) {
		Map<String, Object> props = getPropsFromApplicationPackage(appPackage);
		props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
		props.put(SlingConstants.NAMESPACE_PREFIX + ":" + SlingConstants.PROPERTY_RESOURCE_TYPE, SledgeConstants.RT_PACKAGE);

		Resource packageResource = null;
		try {
			packageResource = resourceResolver.create(getRepositoryRootResource(), appPackage.getPackageFilename(), props);

			resourceResolver.commit();

			props = new HashMap<>();
			props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);

			Resource fileResource = resourceResolver.create(packageResource, "packageFile", props);

			props = new HashMap<>();
			props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);

			BufferedInputStream bin = new BufferedInputStream(appPackage.getPackageFileStream());

			props.put(JcrConstants.JCR_DATA, bin);

			resourceResolver.create(fileResource, JcrConstants.JCR_CONTENT, props);

			resourceResolver.commit();
		} catch (PersistenceException e) {
			throw new RuntimeException("Could not add the Application Package to the repository.", e);
		}
	}

	@Override
	public void updateApplicationPackage(ApplicationPackage appPackage) {
		Resource packageResource;

		try {
			packageResource = getRepositoryRootResource().getChild(appPackage.getPackageFilename());

			if (packageResource != null) {
				ModifiableValueMap props = packageResource.adaptTo(ModifiableValueMap.class);
				props.putAll(getPropsFromApplicationPackage(appPackage));
				resourceResolver.commit();
			}
		} catch (PersistenceException e) {
			throw new RuntimeException("Could not update the Application Package in the repository.", e);
		}
	}

	@Override
	public Resource getRepositoryRootResource() {
		return resourceResolver.getResource(APP_PACKAGES_ROOT_PATH);
	}

	@Override
	public void addOrUpdate(ApplicationPackage appPackage) {
		List<ApplicationPackage> installedPackages = getApplicationPackages();

		boolean update = false;
		for (ApplicationPackage installedPackage : installedPackages) {
			if (installedPackage.getPackageFilename().equals(appPackage.getPackageFilename())) {
				update = true;
				break;
			}
		}
		if (!update) {
			addApplicationPackage(appPackage);
		} else {
			updateApplicationPackage(appPackage);
		}
	}

	private Map<String, Object> getPropsFromApplicationPackage(ApplicationPackage appPackage) {
		Map<String, Object> props = new HashMap<>();
		props.put("groupId", appPackage.getGroupId());
		props.put("artifactId", appPackage.getArtifactId());
		props.put("version", appPackage.getVersion());
		props.put("applicationPackageType",
				appPackage.getApplicationPackageType() == null ? "" : appPackage.getApplicationPackageType().toString());
		props.put("packageFilename", appPackage.getPackageFilename());
		props.put("state", appPackage.getState().toString());
		props.put("usedEnvironment", appPackage.getUsedEnvironment() == null ? "" : appPackage.getUsedEnvironment());

		return props;
	}

	private boolean isAclPrimaryType(Resource appPackageResource) {
		return appPackageResource.getValueMap().get("jcr:primaryType", "").equals("rep:ACL");
	}

	private ApplicationPackageModel createApplicationPackage(Resource appPackageResource) {
		ApplicationPackageModel appPackage = appPackageResource.adaptTo(ApplicationPackageModel.class);
		appPackage.setPackageFilename(appPackageResource.getName());

		return appPackage;
	}
}
