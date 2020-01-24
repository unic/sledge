package io.sledge.deployer.crx

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.common.SledgeConsoleReporter
import io.sledge.deployer.common.endOfRetries
import io.sledge.deployer.common.retry
import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.core.api.Deployer
import io.sledge.deployer.core.api.DeploymentDefinition
import io.sledge.deployer.core.exception.SledgeCommandException
import io.sledge.deployer.http.HttpClient
import io.sledge.deployer.sling.SlingOperations
import io.sledge.deployer.sling.validation.validateResponseBodyForOkAndCreatedStatusCode
import kotlinx.coroutines.runBlocking
import java.io.File

class CrxDeployer : Deployer {

    override fun install(deploymentDefinition: DeploymentDefinition, configuration: Configuration) {
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

        echo("\nInstallation finished.")
    }

    override fun uninstall(deploymentDefinition: DeploymentDefinition, uninstallCleanupPaths: List<String>, configuration: Configuration) {
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

        if (uninstallCleanupPaths.isNotEmpty()) {
            echo("Cleaning up paths...")
            for (jcrPath in uninstallCleanupPaths) {
                SlingOperations().removeResource(jcrPath, validateResponseBodyForOkAndCreatedStatusCode, httpClient, configuration)
                echo("Deleted $jcrPath")
                waitFor(configuration.installUninstallWaitTime)
            }
        }

        echo("\nUninstallation finished.\n")
    }

    private fun waitFor(waitTimeInSeconds: Int) {
        Thread.sleep(waitTimeInSeconds * 1000L)
    }

    private fun executePost(httpClient: HttpClient, command: Command, parameters: Map<String, Any> = emptyMap(), retries: Int, retryDelay: Int) {
        val delayInMilliseconds = retryDelay * 1000L

        runBlocking {
            retry(retries = retries, delay = delayInMilliseconds) {
                try {
                    val params = mergeDefaultParameters(command, parameters)
                    val response = httpClient.postMultipart(command.url(), params)
                    command.validate(response)
                } catch (se: SledgeCommandException) {
                    if (endOfRetries(it)) {
                        SledgeConsoleReporter().writeSledgeCommandExceptionInfo(se)
                    }
                    throw se;
                } catch (e: Exception) {
                    if (endOfRetries(it)) {
                        echo("Request failed. Reason: " + e.localizedMessage)
                    }
                    throw e;
                }
            }
        }
    }

    private fun mergeDefaultParameters(command: Command, parameters: Map<String, Any>): Map<String, Any?> {
        return mapOf("cmd" to command.cmdParam).plus(parameters)
    }
}
