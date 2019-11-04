package io.sledge.deployer.crx

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.core.api.DeploymentDefinition
import io.sledge.deployer.crx.command.*
import io.sledge.deployer.http.HttpClient
import java.io.File

class CrxDeployer {

    fun install(deploymentDefinition: DeploymentDefinition, configuration: Configuration) {
        val httpClient = HttpClient(configuration)
        deploymentDefinition.let {
            for (artifact in it.artifacts) {
                val packageName = VaultPropertiesXmlDataExtractor().getEntryValue(artifact.filePath, "name")

                echo("Uploading ${artifact.filePath}...")
                executePost(httpClient, Upload, mapOf("file" to File(artifact.filePath)), configuration.retries, configuration.retryDelay)
                echo("Uploaded.\n")

                echo("Installing $packageName...")
                executePost(httpClient, Install, mapOf("name" to packageName, "recursive" to "true"), configuration.retries, configuration.retryDelay)
                waitFor(configuration.installUninstallWaitTime)
                echo("Installed.\n")
            }
        }
    }

    fun uninstall(deploymentDefinition: DeploymentDefinition, configuration: Configuration) {
        val httpClient = HttpClient(configuration)
        deploymentDefinition.let {
            for (artifact in it.artifacts) {
                val crxPackageName = VaultPropertiesXmlDataExtractor().getEntryValue(artifact.filePath, "name")

                echo("Uninstalling $crxPackageName...")
                executePost(httpClient, Uninstall, mapOf("name" to crxPackageName), configuration.retries, configuration.retryDelay)
                waitFor(configuration.installUninstallWaitTime)
                echo("Uninstalled.\n")

                echo("Deleting $crxPackageName...")
                waitFor(2)
                executePost(httpClient, Delete, mapOf("name" to crxPackageName), configuration.retries, configuration.retryDelay)
                echo("Deleted.\n")
            }
        }
    }

    private fun waitFor(waitTimeInSeconds: Int) {
        Thread.sleep(waitTimeInSeconds * 1000L)
    }
}
