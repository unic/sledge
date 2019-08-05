package io.sledge.deployer.core.api

data class Configuration(val deploymentDefinition: DeploymentDefinition, val retries: Long, val url: String, val user: String, val password: String, val timeout: Long)
