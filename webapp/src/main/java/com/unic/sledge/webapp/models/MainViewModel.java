package com.unic.sledge.webapp.models;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.unic.sledge.core.api.models.ApplicationPackage;
import com.unic.sledge.core.api.repository.PackageRepository;

/**
 * @author oliver.burkhalter
 */
@Model(adaptables = Resource.class)
public class MainViewModel {

    @Self
    private Resource resource;

    public Iterator<Resource> getConnectorResources() {
        return resource.getParent().getChild("connectors").listChildren();
    }
}
