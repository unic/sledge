package io.sledge.deployer.sling

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.checks.BundleStatusCheck
import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.core.api.Deployer
import io.sledge.deployer.core.api.DeploymentDefinition
import io.sledge.deployer.http.HttpClient
import io.sledge.deployer.sling.validation.validateResponseBodyForOkAndCreatedStatusCode
import java.io.File

/**
 * The SlingDeployer uses the .../install OSGi Installer approach.
 * It simply uploads CRX/Vault packages or also OSGi bundles to a specific path in the JCR repository.
 * The OSGi Installer then picks up the files and executes the installation in the background.
 */
class SlingDeployer : Deployer {

    private val sledgeBaseInstallPath = "/apps/sledge-deployment"
    private val slingInstallSuffixFolderName = "install"

    private val slingOperations = SlingOperations()

    override fun install(deploymentDefinition: DeploymentDefinition, configuration: Configuration) {
        val httpClient = HttpClient(configuration)
        val deploymentFolderName = configuration.appName

        deploymentDefinition.let {
            for (artifact in it.artifacts) {
                echo("Installing ${artifact.filePath}...")

                val url = "${sledgeBaseInstallPath}/${deploymentFolderName}/${slingInstallSuffixFolderName}"
                slingOperations.doPost(url, mapOf("*" to File(artifact.filePath)), validateResponseBodyForOkAndCreatedStatusCode, httpClient, configuration)
                waitFor(configuration.installUninstallWaitTime)

                echo("Installed.\n")
            }
        }

        waitFor(configuration.installUninstallWaitTime * 2)
        BundleStatusCheck(configuration).executeCheck()
        echo("\nInstallation finished.")
    }

    override fun uninstall(deploymentDefinition: DeploymentDefinition, uninstallCleanupPaths: List<String>, configuration: Configuration) {
        val httpClient = HttpClient(configuration)
        val deploymentFolderName = configuration.appName

        echo("Uninstalling everything in ${sledgeBaseInstallPath}/${deploymentFolderName}\n")

        val deploymentFolderAbsolutePath = "${sledgeBaseInstallPath}/${deploymentFolderName}"
        slingOperations.removeResource(deploymentFolderAbsolutePath, validateResponseBodyForOkAndCreatedStatusCode, httpClient, configuration)
        waitFor(configuration.installUninstallWaitTime * 2)

        if (uninstallCleanupPaths.isNotEmpty()) {
            echo("Cleaning up paths...")
            for (jcrPath in uninstallCleanupPaths) {
                slingOperations.removeResource(jcrPath, validateResponseBodyForOkAndCreatedStatusCode, httpClient, configuration)
                echo("Deleted $jcrPath")
                waitFor(configuration.installUninstallWaitTime)
            }
        }

        BundleStatusCheck(configuration).executeCheck()
        echo("\nUninstallation finished.\n")
    }

    private fun waitFor(waitTimeInSeconds: Int) {
        Thread.sleep(waitTimeInSeconds * 1000L)
    }
}