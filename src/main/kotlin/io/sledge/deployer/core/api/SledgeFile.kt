package io.sledge.deployer.core.api

class SledgeFile(val deploymentName: String,
                 val deployerImplementation: DeployerImplementation,
                 val artifactsPathPrefix: String,
                 private val deploymentDefinitions: List<DeploymentDefinition>) {

    fun findDeploymentDefinitionByName(deploymentDefName: String): DeploymentDefinition? {
        return deploymentDefinitions.find { item ->
            item.name.equals(deploymentDefName, true)
        }
    }
}
