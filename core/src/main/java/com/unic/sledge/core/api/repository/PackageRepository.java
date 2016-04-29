package com.unic.sledge.core.api.repository;

import java.util.List;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import com.unic.sledge.core.api.models.ApplicationPackage;

/**
 * Defines a package repository where the app manages all the loaded {@link ApplicationPackage} resources.
 * 
 * @author oliver.burkhalter
 */
public interface PackageRepository {

    List<ApplicationPackage> getApplicationPackages();

    void addApplicationPackage(ApplicationPackage appPackage);

    void updateApplicationPackage(ApplicationPackage appPackage);

    /**
     * @return Returns the root node of the repository of the packages. Mostly /etc/sledge/packages.
     */
    Resource getRepositoryRootResource();
}
