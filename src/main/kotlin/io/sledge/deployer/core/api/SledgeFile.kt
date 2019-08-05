package io.sledge.deployer.core.api

class SledgeFile(private val artifactsPathPrefix: String, private val deploymentDefinitions: List<DeploymentDefinition>) {

    fun findDeploymentDefinitionByName(deploymentDefName: String): DeploymentDefinition? {
        return deploymentDefinitions.find { dd ->
            dd.name.equals(deploymentDefName, true)
        }
    }
}
