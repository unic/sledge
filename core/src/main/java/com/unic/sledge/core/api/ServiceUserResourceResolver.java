package com.unic.sledge.core.api;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * Provides a resource resolver based on a service user.
 * 
 * @author oliver.burkhalter
 */
public interface ServiceUserResourceResolver {

    /**
     * Gets a service resource resolver with the a configured user.
     * This resolver should not be closed by other services, else it leads to 'resolver already closed' issues.
     * 
     * @return Returns the service resource resolver of a configured user.
     */
    ResourceResolver getServiceResourceResolver();
}
