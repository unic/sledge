package io.sledge.deployer.console

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.core.api.Deployer
import io.sledge.deployer.core.api.DeployerImplementation.crx
import io.sledge.deployer.core.api.DeployerImplementation.sling
import io.sledge.deployer.core.api.SledgeFile
import io.sledge.deployer.core.api.SledgeFileParser
import io.sledge.deployer.crx.CrxDeployer
import io.sledge.deployer.sling.SlingDeployer
import java.io.File


private fun parseSledgeFile(sledgeFileParser: SledgeFileParser): SledgeFile {
    val yamlSledgeFile = sledgeFileParser.parseSledgeFile(File("deployment-configuration.yaml"))
    echo("deployment-configuration.yaml parsed.\n")
    return yamlSledgeFile
}

private fun createConfiguration(deploymentName: String, targetServerUrl: String, config: Map<String, Any>): Configuration {
    return Configuration(
            deploymentName,
            targetServerUrl,
            config["user"] as String,
            config["password"] as String,
            config["retries"] as Int,
            config["retryDelay"] as Int,
            config["installUninstallWaitTime"] as Int,
            config["callTimeout"] as Int)
}

private fun initDeployer(yamlSledgeFile: SledgeFile): Deployer {
    return when (yamlSledgeFile.deployerImplementation) {
        crx -> CrxDeployer()
        sling -> SlingDeployer()
    }
}

class SledgeCommand() : CliktCommand(name = "sledge") {
    private val user by option(help = "Sling/AEM user. Defaults to admin").default("admin")
    private val password by option(help = "Sling/AEM user. Defaults to admin").default("admin")
    private val retries by option(help = "Number of retries to re-execute the given request. Defaults to 10 times").int().default(10)
    private val retryDelay by option(help = "Time to delay between retries. Defaults to 3 seconds").int().default(3)
    private val installUninstallWaitTime by option(help = "Time to wait after installation or uninstallation of a package. Defaults to 2 seconds").int().default(2)
    private val callTimeout by option(help = "Time to use to fulfill the complete request and response processing. Defaults to 6 seconds").int().default(6)

    private val config by findObject { mutableMapOf<String, Any>() }

    override fun run() {
        echo("Working dir: ${System.getProperty("user.dir")}")

        config["user"] = user
        config["password"] = password
        config["retries"] = retries
        config["retryDelay"] = retryDelay
        config["installUninstallWaitTime"] = installUninstallWaitTime
        config["callTimeout"] = callTimeout
    }
}

class Install(private val sledgeFileParser: SledgeFileParser) : CliktCommand(help = "Installs the defined packages in the deployment-configuration.yaml.") {
    private val deploymentDefinitionName by argument(name = "DEPLOYMENT_DEFINITION_NAME", help = "The name of the deployment definition, e.g. local, dev-auhor, dev-publish, test, prod, etc.")
    private val targetServerUrl by argument(name = "TARGET_SERVER_URL", help = "The url to the target server, e.g. http://server:4502")

    private val config by requireObject<Map<String, Any>>()

    override fun run() {
        echo("Deployment definition name: $deploymentDefinitionName")
        echo("Target server: $targetServerUrl\n")

        val yamlSledgeFile = parseSledgeFile(sledgeFileParser)
        val deployer = initDeployer(yamlSledgeFile)
        val deploymentDefinition = yamlSledgeFile.findDeploymentDefinitionByName(deploymentDefinitionName)

        if (deploymentDefinition == null) {
            echo("Error: Could not find any valid deployment definition with the name $deploymentDefinitionName")
        } else {
            echo("Deployer implementation: ${yamlSledgeFile.deployerImplementation.name}\n")
            deployer.install(deploymentDefinition, createConfiguration(yamlSledgeFile.deploymentName, targetServerUrl, config))
        }
    }
}

class Uninstall(private val sledgeFileParser: SledgeFileParser) : CliktCommand(help = "Uninstalls the defined list of packages in the deployment-configuration.yaml") {
    private val deploymentDefinitionName by argument(name = "DEPLOYMENT_DEFINITION_NAME", help = "The name of the deployment definition, e.g. local, dev-auhor, dev-publish, test, prod, etc.")
    private val targetServerUrl by argument(name = "TARGET_SERVER_URL", help = "The url to the target server, e.g. http://server:4502")

    private val config by requireObject<Map<String, Any>>()

    override fun run() {
        echo("Deployment definition name: $deploymentDefinitionName")
        echo("Target server: $targetServerUrl\n")

        val yamlSledgeFile = parseSledgeFile(sledgeFileParser)
        val deployer = initDeployer(yamlSledgeFile)
        val deploymentDefinition = yamlSledgeFile.findDeploymentDefinitionByName(deploymentDefinitionName)

        if (deploymentDefinition == null) {
            echo("Error: Could not find any valid deployment definition with the name $deploymentDefinitionName")
        } else {
            echo("Deployer implementation: ${yamlSledgeFile.deployerImplementation.name}\n")
            deployer.uninstall(deploymentDefinition, createConfiguration(yamlSledgeFile.deploymentName, targetServerUrl, config))
        }
    }
}
