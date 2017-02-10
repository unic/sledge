package io.sledge.core.impl.installer;

import io.sledge.core.api.ApplicationPackage;
import io.sledge.core.api.SledgeConstants;
import io.sledge.core.api.installer.UninstallationException;
import io.sledge.core.api.installer.Uninstaller;
import io.sledge.core.api.models.ApplicationPackageType;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Simply removes the package from the Sledge install location.
 */
public class GenericUninstaller implements Uninstaller {

    private final ResourceResolver resourceResolver;

    public GenericUninstaller(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    @Override
    public void uninstall(ApplicationPackage applicationPackage) {
        String packagePath = SledgeConstants.SLEDGE_INSTALL_LOCATION + "/" + applicationPackage.getPackageFilename();
        Resource packageResource = resourceResolver.getResource(packagePath);

        try {

            if (packageResource != null) {
                resourceResolver.delete(packageResource);
            }
            resourceResolver.commit();

        } catch (PersistenceException ex) {
            throw new UninstallationException("Could not uninstall generic Application package properly.", ex);
        }
    }

    @Override
    public boolean handles(ApplicationPackage appPackage) {
        return appPackage.getApplicationPackageType().equals(ApplicationPackageType.generic);
    }
}
