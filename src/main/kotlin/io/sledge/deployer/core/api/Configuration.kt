package io.sledge.deployer.core.api

data class Configuration(
        val deploymentName: String,
        val targetUrl: String,
        val user: String,
        val password: String,
        val retries: Int,
        val retryDelay: Int,
        val installUninstallWaitTime: Int,
        val callTimeout: Int,
        val initBundleStatusRetries: Int = 6,
        val initBundleStatusRetryDelay: Int = 3)
