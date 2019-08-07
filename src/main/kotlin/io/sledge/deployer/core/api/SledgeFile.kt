package io.sledge.deployer.core.api

class SledgeFile(val artifactsPathPrefix: String, val uninstallationDefinitions: List<String>, private val installationDefinitions: List<InstallationDefinition>) {

    fun findInstallationDefinitionByName(deploymentDefName: String): InstallationDefinition? {
        return installationDefinitions.find { item ->
            item.name.equals(deploymentDefName, true)
        }
    }
}
