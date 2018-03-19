package io.sledge.core.impl.installer;

import io.sledge.core.api.ApplicationPackage;
import io.sledge.core.api.SledgeConstants;
import io.sledge.core.api.installer.InstallationException;
import io.sledge.core.api.installer.Installer;
import io.sledge.core.api.models.ApplicationPackageType;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Installs generic packages such as CRX/Vault package or simply an OSGi bundle.
 */
public class GenericInstaller implements Installer {

	private static final Logger LOG = LoggerFactory.getLogger(GenericInstaller.class);

	private final ResourceResolver resourceResolver;

	public GenericInstaller(ResourceResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	@Override
	public void install(ApplicationPackage appPackage) throws InstallationException {
		Resource defaultInstallLocationResource = resourceResolver.getResource(SledgeConstants.SLEDGE_INSTALL_LOCATION);

		try {
			Map<String, Object> props = new HashMap<>();
			props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);

			String packageResourcePath = defaultInstallLocationResource.getPath() + "/" + appPackage.getPackageFilename();
			if (!packageResourceExists(packageResourcePath)) {
				Resource fileResource = resourceResolver.create(defaultInstallLocationResource, appPackage.getPackageFilename(), props);

				props = new HashMap<>();
				props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
				props.put(JcrConstants.JCR_DATA, appPackage.getPackageFileStream());
				resourceResolver.create(fileResource, JcrConstants.JCR_CONTENT, props);

				resourceResolver.commit();
			} else {
				LOG.warn("Package: " + packageResourcePath
						+ " has not been installed because it exists already. If you want to update your application, please uninstall first.");
			}
		} catch (PersistenceException e) {
			throw new InstallationException("Could not install generic package: " + appPackage.getPackageFilename(), e);
		}
	}

	@Override
	public boolean handles(ApplicationPackage appPackage) {
		return appPackage.getApplicationPackageType().equals(ApplicationPackageType.generic);
	}

	private boolean packageResourceExists(String packageResourcePath) {
		return resourceResolver.getResource(packageResourcePath) != null;
	}
}
