package com.unic.sledge.core.impl;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unic.sledge.core.api.ServiceUserResourceResolver;

/**
 * @author oliver.burkhalter
 */
@Component(immediate = true)
@Service(value = ServiceUserResourceResolver.class)
public class SledgeServiceResourceResolver implements ServiceUserResourceResolver {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String SLEDGE_SUBSERVICE_NAME = "sledgeReadWrite";

    public static final String SERVICE_USERID = "sledgeUser";

    @Reference
    private SlingRepository repository;

    @Reference(policy = ReferencePolicy.DYNAMIC)
    private ResourceResolverFactory resolverFactory;

    private ResourceResolver resourceResolver;

    private boolean serviceUserCreated = false;

    @Activate
    private void activate(BundleContext context, Map<String, Object> config) {
        String userId = SERVICE_USERID;

        Session session = null;
        try {

            // NOTE: There is an ongoing discussion when to use loginAdministrative(..), see here:
            // https://issues.apache.org/jira/browse/SLING-5135
            session = repository.loginAdministrative(repository.getDefaultWorkspace());

            if (!serviceUserExists(session, userId)) {
                createServiceUser(userId, session);
                serviceUserCreated = true;

            } else {
                log.info("Sledge service user does exist already, using this service user: " + userId);
                serviceUserCreated = true;
            }

            setResourceResolver();

        } catch (LoginException e) {
            log.warn("Could not login with Sledge service user. " + e.getMessage(), e);
        } catch (Exception e) {
            log.warn("Could not create service user for Sledge. " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.logout();
            }
        }

    }

    @Deactivate
    public void deactivate() {
        closeResourceResolver();
    }

    @Override
    public ResourceResolver getServiceResourceResolver() {
        if (resourceResolver == null) {
            throw new IllegalStateException("No valid resource resolver is currently available. Please retry later.");
        }
        return resourceResolver;
    }

    private void createServiceUser(String userId, Session session) throws AuthorizableExistsException, RepositoryException {
        UserManager userManager = getUserManager(session);
        userManager.createSystemUser(userId, null);
        session.save();
        log.info("Sledge service user has been created. UserId=" + userId);
    }

    private void setResourceResolver() throws LoginException {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, SLEDGE_SUBSERVICE_NAME);
        resourceResolver = resolverFactory.getServiceResourceResolver(param);

        log.info("Logged in as Sledge user: " + resourceResolver.getUserID());
    }

    private boolean serviceUserExists(Session session, String username) throws RepositoryException {
        boolean result = false;
        final Authorizable a = getAuthorizable(session, username);
        if (a != null) {
            final User u = (User) a;
            result = u.isSystemUser();
        }
        return result;
    }

    private Authorizable getAuthorizable(Session session, String username) throws RepositoryException {
        return getUserManager(session).getAuthorizable(username);
    }

    private UserManager getUserManager(Session session) throws RepositoryException {
        if (!(session instanceof JackrabbitSession)) {
            throw new IllegalArgumentException("Session is not a JackrabbitSession");
        }
        return ((JackrabbitSession) session).getUserManager();
    }

    public void bindResolverFactory(ResourceResolverFactory factory) throws LoginException {
        resolverFactory = factory;

        if (serviceUserCreated) {
            setResourceResolver();
        }
    }

    public void unbindResolverFactory(ResourceResolverFactory factory) {
        closeResourceResolver();
    }

    private void closeResourceResolver() {
        if (resourceResolver != null && resourceResolver.isLive()) {
            resourceResolver.close();
        }
    }

}
