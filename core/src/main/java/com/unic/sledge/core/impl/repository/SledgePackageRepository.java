package com.unic.sledge.core.impl.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.unic.sledge.core.api.ApplicationPackage;
import com.unic.sledge.core.api.ServiceUserResourceResolver;
import com.unic.sledge.core.api.repository.PackageRepository;

/**
 * Defines the central package repository of the Sledge app.
 * <p>
 * By default this is: <code>/etc/sledge/packages</code>.
 * 
 * @author oliver.burkhalter
 */
@Component(immediate = true)
@Service(value = PackageRepository.class)
public class SledgePackageRepository implements PackageRepository {

    public static final String APP_PACKAGES_ROOT_PATH = "/etc/sledge/packages";

    @Reference
    private ServiceUserResourceResolver serviceUserResourceResolver;

    @Override
    public List<ApplicationPackage> getApplicationPackages() {
        List<ApplicationPackage> appPackages = new ArrayList<>();

        ResourceResolver resourceResolver = serviceUserResourceResolver.getServiceResourceResolver();
        if (resourceResolver == null) {
            throw new IllegalArgumentException("Cannot get application packages because no valid resource resolver is available.");
        }

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
    public void addApplicationPackage(ApplicationPackage appPackage) throws PersistenceException {
        Map<String, Object> props = new HashMap<>();
        props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
        props.put("packageType", "application");
        props.put("artifactId", appPackage.getArtifactId());
        props.put("groupId", appPackage.getGroupId());

        ResourceResolver resolver = serviceUserResourceResolver.getServiceResourceResolver();

        // TODO: Handle duplicate file names

        Resource packageResource = resolver.create(getRepositoryRootResource(), appPackage.getPackageFilename(), props);

        resolver.commit();

        props = new HashMap<>();
        props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);

        Resource fileResource = resolver.create(packageResource, "packageFile", props);

        props = new HashMap<>();
        props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
        props.put(JcrConstants.JCR_DATA, appPackage.getPackageFile());

        resolver.create(fileResource, JcrConstants.JCR_CONTENT, props);

        resolver.commit();
    }

    @Override
    public Resource getRepositoryRootResource() {
        ResourceResolver resourceResolver = serviceUserResourceResolver.getServiceResourceResolver();
        return resourceResolver.getResource(APP_PACKAGES_ROOT_PATH);
    }

    private boolean isAclPrimaryType(Resource appPackageResource) {
        return appPackageResource.getValueMap().get("jcr:primaryType", "").equals("rep:ACL");
    }

    private ApplicationPackage createApplicationPackage(Resource appPackageResource) {
        ApplicationPackage appPackage = new ApplicationPackage(appPackageResource.getName());
        appPackage.setArtifactId(appPackageResource.getValueMap().get("artifactId", "NO_ARTIFACT_ID"));
        appPackage.setGroupId(appPackageResource.getValueMap().get("groupId", "nogroup"));

        return appPackage;
    }

}
