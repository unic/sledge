package io.sledge.deployer.http

data class SledgeHttpResponse(val isSuccessful: Boolean, val statusCode: Int, val bodayAsString: String)