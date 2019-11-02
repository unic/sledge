package io.sledge.deployer.exception

open class SledgeCommandException(override val message: String, val requestUrl: String, val responseCode: Int, val responseBody: String) : RuntimeException(message) {
}