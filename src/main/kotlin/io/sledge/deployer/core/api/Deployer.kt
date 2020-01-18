package io.sledge.deployer.core.api

interface Deployer {

    fun install(deploymentDefinition: DeploymentDefinition, configuration: Configuration)

    fun uninstall(deploymentDefinition: DeploymentDefinition, configuration: Configuration)
}
