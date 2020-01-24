package io.sledge.deployer.sling.validation

import io.sledge.deployer.core.exception.SledgeCommandException
import io.sledge.deployer.http.SledgeHttpResponse

val validateResponseBodyForOkAndCreatedStatusCode: (response: SledgeHttpResponse, url: String) -> Unit = { response, url ->
    val responseBody = response.bodayAsString
    val validResponseBodyTexts = setOf("200", "OK", "201", "Created")

    if (validResponseBodyTexts.any { it in responseBody }) else {
        throw SledgeCommandException("Response did not contain expected validation text.", url, response.statusCode, responseBody)
    }
}