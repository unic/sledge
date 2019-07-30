package io.sledge.deployer.crx

import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.core.api.DeploymentDefinition

class CrxConfiguration(val deploymentDefinition: DeploymentDefinition, val deploymentMode: String?, val retries: String?, url: String, user: String = "admin", password: String= "admin", timeout: Long): Configuration(url, user, password, timeout)
