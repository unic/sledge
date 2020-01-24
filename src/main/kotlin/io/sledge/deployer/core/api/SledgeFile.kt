package io.sledge.deployer.core.api

class SledgeFile(val appName: String,
                 val deployerImplementation: DeployerImplementation,
                 val artifactsPathPrefix: String,
                 val uninstallCleanupPaths: List<String>,
                 private val deploymentDefinitions: List<DeploymentDefinition>) {

    fun findDeploymentDefinitionByName(deploymentDefName: String): DeploymentDefinition? {
        return deploymentDefinitions.find { item ->
            item.name.equals(deploymentDefName, true)
        }
    }
}
