package io.sledge.deployer.sling

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.checks.BundleStatusCheck
import io.sledge.deployer.common.SledgeConsoleReporter
import io.sledge.deployer.common.endOfRetries
import io.sledge.deployer.common.retry
import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.core.api.Deployer
import io.sledge.deployer.core.api.DeploymentDefinition
import io.sledge.deployer.core.exception.SledgeCommandException
import io.sledge.deployer.http.HttpClient
import io.sledge.deployer.http.SledgeHttpResponse
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * The SlingDeployer uses the .../install OSGi Installer approach.
 * It simply uploads CRX/Vault packages or also OSGi bundles to a specific path in the JCR repository.
 * The OSGi Installer then picks up the files and executes the installation in the background.
 */
class SlingDeployer : Deployer {

    private val sledgeBaseInstallPath = "/apps/sledge-deployment"
    private val slingInstallSuffixFolderName = "install"

    override fun install(deploymentDefinition: DeploymentDefinition, configuration: Configuration) {
        val httpClient = HttpClient(configuration)
        val deploymentFolderName = configuration.deploymentName

        deploymentDefinition.let {
            for (artifact in it.artifacts) {
                echo("Installing ${artifact.filePath}...")

                val url = "${sledgeBaseInstallPath}/${deploymentFolderName}/${slingInstallSuffixFolderName}"
                doPost(url, mapOf("*" to File(artifact.filePath)), httpClient, configuration)
                waitFor(configuration.installUninstallWaitTime)

                echo("Installed.\n")
            }
        }

        waitFor(configuration.installUninstallWaitTime * 2)
        BundleStatusCheck(configuration).executeCheck()
        echo("\nInstallation finished.")
    }

    override fun uninstall(deploymentDefinition: DeploymentDefinition, configuration: Configuration) {
        val httpClient = HttpClient(configuration)
        val deploymentFolderName = configuration.deploymentName

        echo("Uninstalling everything in ${sledgeBaseInstallPath}/${deploymentFolderName}")

        val url = "${sledgeBaseInstallPath}/${deploymentFolderName}"
        doPost(url, mapOf(":operation" to "delete"), httpClient, configuration)
        waitFor(configuration.installUninstallWaitTime * 2)

        BundleStatusCheck(configuration).executeCheck()
        echo("\nUninstallation finished.\n")
    }

    private fun doPost(url: String = "", parameters: Map<String, Any> = emptyMap(), httpClient: HttpClient, configuration: Configuration) {
        val delayInMilliseconds = configuration.retryDelay * 1000L

        runBlocking {
            retry(retries = configuration.retries, delay = delayInMilliseconds) {
                try {
                    val response = httpClient.postMultipart(url, parameters)
                    validateResponse(response, url)
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

    private fun validateResponse(response: SledgeHttpResponse, url: String) {
        val responseBody = response.bodayAsString
        val validResponseBodyTexts = setOf("200", "OK", "201", "Created")

        if (validResponseBodyTexts.any { it in responseBody }) else {
            throw SledgeCommandException("Response did not contain expected validation text.", url, response.statusCode, responseBody)
        }
    }

    private fun waitFor(waitTimeInSeconds: Int) {
        Thread.sleep(waitTimeInSeconds * 1000L)
    }
}