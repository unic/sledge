package io.sledge.deployer.crx

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.crx.command.*
import io.sledge.deployer.http.HttpClient
import io.sledge.deployer.zip.Unarchiver
import io.sledge.deployer.zip.Unarchiver.Companion.VLT_PROPERTIES
import java.io.File

class CrxDeployer {

    fun deploy(configuration: CrxConfiguration) {

        if (configuration == null) {
            echo("ERROR: Deployment definition name not found in Sledge file.", err = true)
        }

        val httpClient = HttpClient(configuration)
        configuration.deploymentDefinition.let {
            echo("Start deployment for ${configuration.deploymentDefinition.name}")
            for (artifact in configuration.deploymentDefinition.deploymentArtifacts) {
                val packageName = Unarchiver().unzipPropertiesFile(artifact.filePath, VLT_PROPERTIES)
                executePost(httpClient, Uninstall, packageName)
                executePost(httpClient, Remove, "x")
                executePost(httpClient, Upload, "x", Pair("file", File(artifact.filePath)))
                executePost(httpClient, Install, packageName)
            }
        }

    }
}
