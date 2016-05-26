package com.unic.sledge.core.impl.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.unic.sledge.core.api.models.ApplicationPackageState;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.unic.sledge.core.api.SledgeConstants;
import com.unic.sledge.core.api.models.ApplicationPackage;
import com.unic.sledge.core.api.repository.PackageRepository;

/**
 * Defines the central package repository of the Sledge app.
 * <p>
 * By default this is: <code>/etc/sledge/packages</code>.
 * 
 * @author oliver.burkhalter
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

        // TODO: Handle duplicate file names

		Resource packageResource = null;
		try {
			packageResource = resourceResolver.create(getRepositoryRootResource(), appPackage.getPackageFilename(), props);

			resourceResolver.commit();

			props = new HashMap<>();
			props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);

			Resource fileResource = resourceResolver.create(packageResource, "packageFile", props);

			props = new HashMap<>();
			props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
			props.put(JcrConstants.JCR_DATA, appPackage.getPackageFile());

			resourceResolver.create(fileResource, JcrConstants.JCR_CONTENT, props);

			resourceResolver.commit();

		} catch (PersistenceException e) {
			throw new RuntimeException("Could not add the Application Package to the repository.", e);
		}

    }

	@Override
	public void updateApplicationPackage(ApplicationPackage appPackage) {
		Resource packageResource = null;

		try {
			packageResource = getRepositoryRootResource().getChild(appPackage.getPackageFilename());

			if(packageResource != null) {
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

	private Map<String, Object> getPropsFromApplicationPackage(ApplicationPackage appPackage) {
		Map<String, Object> props = new HashMap<>();
		props.put("packageFilename", appPackage.getPackageFilename());
		props.put("state", appPackage.getState().toString());

		return props;
	}

    private boolean isAclPrimaryType(Resource appPackageResource) {
        return appPackageResource.getValueMap().get("jcr:primaryType", "").equals("rep:ACL");
    }

    private ApplicationPackage createApplicationPackage(Resource appPackageResource) {
        ApplicationPackage appPackage = new ApplicationPackage(appPackageResource.getName());
        appPackage.setPath(appPackageResource.getPath());

        return appPackage;
    }

}
