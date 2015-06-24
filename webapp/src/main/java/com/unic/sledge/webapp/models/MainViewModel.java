package com.unic.sledge.webapp.models;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.unic.sledge.core.api.ApplicationPackage;
import com.unic.sledge.core.api.repository.PackageRepository;

/**
 * @author oliver.burkhalter
 */
@Model(adaptables = Resource.class)
public class MainViewModel {

    @Self
    private Resource resource;

    @Inject
    private PackageRepository packageRepository;

    public List<ApplicationPackage> getApplicationPackages() {
        return packageRepository.getApplicationPackages();
    }

    public Iterator<Resource> getConnectorResources() {
        return resource.getParent().getChild("connectors").listChildren();
    }
}
