package com.unic.sledge.core.impl.installer;

import com.unic.sledge.core.api.SledgeConstants;
import com.unic.sledge.core.api.extractor.ApplicationPackageExtractor;
import com.unic.sledge.core.impl.extractor.SledgeApplicationPackageExtractor;
import com.unic.sledge.core.api.installer.InstallationException;
import com.unic.sledge.core.api.installer.Installer;
import com.unic.sledge.core.api.models.ApplicationPackage;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author oliver.burkhalter
 */
public class SledgeInstallerImpl implements Installer {

	private ResourceResolver resourceResolver;

	public SledgeInstallerImpl(ResourceResolver resourceResolver) {
		if(resourceResolver == null) {
			throw new IllegalArgumentException("Cannot initialize SledgeInstallerImpl because the ResourceResolver is null.");
		}

		this.resourceResolver = resourceResolver;
	}

	@Override
	public void install(ApplicationPackage appPackage) throws InstallationException {

		Resource installLocationResource = resourceResolver.getResource(SledgeConstants.SLEDGE_INSTALL_LOCATION);

		ApplicationPackageExtractor appPackageExtractor = new SledgeApplicationPackageExtractor();

		Map<String, InputStream> packages = appPackageExtractor.getPackages(appPackage);

		for (Map.Entry<String, InputStream> packageEntry : packages.entrySet()) {
			try {

				Map<String, Object> props = new HashMap<>();
				props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);
				Resource fileResource = resourceResolver.create(installLocationResource, packageEntry.getKey(), props);

				props = new HashMap<>();
				props.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
				props.put(JcrConstants.JCR_DATA, packageEntry.getValue());
				resourceResolver.create(fileResource, JcrConstants.JCR_CONTENT, props);

				resourceResolver.commit();

			} catch (PersistenceException e) {
				throw new InstallationException("Could not install package: " + packageEntry.getKey(), e);
			}
		}
	}
}
