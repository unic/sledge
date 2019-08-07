package io.sledge.deployer.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.core.api.SledgeFile
import io.sledge.deployer.core.api.SledgeFileParser
import io.sledge.deployer.crx.CrxDeployer
import java.io.File


private fun parseSledgeFile(sledgeFileParser: SledgeFileParser): SledgeFile {
    val yamlSledgeFile = sledgeFileParser.parseSledgeFile(File("deployment-configuration.yaml"))
    echo("deployment-configuration.yaml parsed.\n")
    return yamlSledgeFile
}

private fun createConfiguration(targetServerUrl: String, config: Map<String, Any>): Configuration {
    return Configuration(targetServerUrl, config["user"] as String, config["password"] as String, config["retries"] as Int, config["timeout"] as Int)
}

class SledgeCommand() : CliktCommand(name = "sledge") {
    private val user by option(help = "Defaults to admin").default("admin")
    private val password by option(help = "Defaults to admin").default("admin")
    private val timeout by option(help = "Defaults to 8 seconds").int().default(8)
    private val retries by option(help = "Defaults to 5 times").int().default(5)

    private val config by findObject { mutableMapOf<String, Any>() }

    override fun run() {
        config["user"] = user
        config["password"] = password
        config["timeout"] = timeout
        config["retries"] = retries
    }
}

class Install(private val sledgeFileParser: SledgeFileParser, private val deployer: CrxDeployer) : CliktCommand(help = "Installs the defined packages in the deployment-configuration.yaml.") {
    private val installationDefinitionName by argument(name = "INSTALLATION_DEFINITION_NAME", help = "The name of the installation definition, e.g. local, dev-auhor, dev-publish, test, prod, etc.")
    private val targetServerUrl by argument(name = "TARGET_SERVER_URL", help = "The url to the target server, e.g. http://server:4502")

    private val config by requireObject<Map<String, Any>>()

    override fun run() {
        echo("Working dir: ${System.getProperty("user.dir")}")
        echo("Installation definition name: $installationDefinitionName")
        echo("Target server: $targetServerUrl\n")

        val yamlSledgeFile = parseSledgeFile(sledgeFileParser)
        val installationDefinition = yamlSledgeFile.findInstallationDefinitionByName(installationDefinitionName)

        if (installationDefinition == null) {
            echo("Error: Could not find any valid installation definition with the name $installationDefinitionName")
        } else {
            deployer.install(installationDefinition, createConfiguration(targetServerUrl, config))
        }
    }
}

class Uninstall(private val sledgeFileParser: SledgeFileParser, private val deployer: CrxDeployer) : CliktCommand(help = "Uninstalls the defined list of artifactIds or package names in the deployment-configuration.yaml") {
    private val targetServerUrl by argument(name = "TARGET_SERVER_URL", help = "The url to the target server, e.g. http://server:4502")

    private val config by requireObject<Map<String, Any>>()

    override fun run() {
        echo("Working dir: ${System.getProperty("user.dir")}")
        echo("Target server: $targetServerUrl")

        val yamlSledgeFile = parseSledgeFile(sledgeFileParser)

        deployer.uninstall(yamlSledgeFile.uninstallationDefinitions, createConfiguration(targetServerUrl, config))
    }
}
