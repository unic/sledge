package io.sledge.core.api.connectors;

import java.util.List;

import io.sledge.core.api.models.ApplicationPackage;

/**
 * @author oliver.burkhalter
 */
public interface PackageSourceConnector {

    /**
     * Loads and converts Application package artifacts from the remote source.
     * 
     * @return Returns a list of loaded Application packages from the remote source.
     */
    List<ApplicationPackage> loadApplicationPackages();

}
