package io.sledge.deployer.crx

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.crx.command.*
import io.sledge.deployer.http.HttpClient
import io.sledge.deployer.crx.zip.Unarchiver
import io.sledge.deployer.crx.zip.Unarchiver.Companion.VLT_PROPERTIES
import java.io.File

class CrxDeployer {

    fun deploy(configuration: Configuration) {
        val httpClient = HttpClient(configuration)
        configuration.deploymentDefinition.let {
            echo("Start deployment for ${configuration.deploymentDefinition.name}")
            for (artifact in configuration.deploymentDefinition.deploymentArtifacts) {
                val packageName = Unarchiver().extractPackageName(artifact.filePath, VLT_PROPERTIES)
                executePost(httpClient, Uninstall, packageName, configuration.retries)
                executePost(httpClient, Remove, packageName, configuration.retries)
                executePost(httpClient, Upload, packageName, configuration.retries, Pair("file", File(artifact.filePath)))
                executePost(httpClient, Install, packageName, configuration.retries)
            }
        }

    }
}
