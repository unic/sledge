package io.sledge.deployer.core.api

data class Configuration(
        val targetUrl: String,
        val user: String,
        val password: String,
        val retries: Int,
        val retryDelay: Int,
        val installUninstallWaitTime: Int,
        val callTimeout: Int)
