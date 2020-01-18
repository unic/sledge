package io.sledge.deployer.core.api

/**
 * Defines the different deployment approaches for Sling/AEM applications.
 *
 * crx = Uses AEM's CRX Package Manager API interface and can only handle crx/vault packages
 * sling = Uses the Sling OSGi Installer directly and can handle both, crx/vault and OSGi bundles
 */
enum class DeployerImplementation {
    crx, sling
}