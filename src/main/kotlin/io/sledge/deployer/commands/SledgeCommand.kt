package io.sledge.deployer.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import io.sledge.deployer.core.api.Deployer
import io.sledge.deployer.core.api.Deployment
import io.sledge.deployer.core.api.SledgeFileParser
import java.io.File

class SledgeCommand(val sledgeFileParser: SledgeFileParser, val deployer: Deployer) : CliktCommand(name = "sledge") {
    private val deploymentDefinitionName by argument(name = "DEPLOYMENT_DEFINITION_NAME", help = "The name of the Deployment definition, e.g. local, dev-auhor, dev-publish, test, prod, etc.")
    private val targetServer by argument(name = "TARGET_SERVER", help = "The url to the target server, e.g. http://server:4502")
    val user by option()
    val password by option()

    override fun run() {
        echo("Working dir: ${System.getProperty("user.dir")}")

        val yamlSledgeFile = sledgeFileParser.parseSledgeFile(File("sledgefile.yaml"))

        val deploymentDefinition = yamlSledgeFile.findDeploymentDefinitionByName(deploymentDefinitionName)

        if (deploymentDefinition == null) {
            echo("ERROR: Deployment definition name not found in Sledge file.", err = true)
        } else {
            deployer.deploy(Deployment(deploymentDefinition, targetServer))
        }
    }
}
