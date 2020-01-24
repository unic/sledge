package io.sledge.deployer.sling

import com.github.ajalt.clikt.output.TermUi
import io.sledge.deployer.common.SledgeConsoleReporter
import io.sledge.deployer.common.endOfRetries
import io.sledge.deployer.common.retry
import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.core.exception.SledgeCommandException
import io.sledge.deployer.http.HttpClient
import io.sledge.deployer.http.SledgeHttpResponse
import kotlinx.coroutines.runBlocking

class SlingOperations {

    fun removeResource(resourcePath: String,
                       validateResponse: (response: SledgeHttpResponse, url: String) -> Unit,
                       httpClient: HttpClient,
                       configuration: Configuration) {
        doPost("", mapOf(":operation" to "delete", ":applyTo" to resourcePath), validateResponse, httpClient, configuration)
    }

    fun doPost(urlPath: String,
               parameters: Map<String, Any> = emptyMap(),
               validateResponse: (response: SledgeHttpResponse, url: String) -> Unit,
               httpClient: HttpClient,
               configuration: Configuration) {

        val delayInMilliseconds = configuration.retryDelay * 1000L

        runBlocking {
            retry(retries = configuration.retries, delay = delayInMilliseconds) {
                try {
                    val response = httpClient.postMultipart(urlPath, parameters)
                    validateResponse(response, urlPath)
                } catch (se: SledgeCommandException) {
                    if (endOfRetries(it)) {
                        SledgeConsoleReporter().writeSledgeCommandExceptionInfo(se)
                    }
                    throw se;
                } catch (e: Exception) {
                    if (endOfRetries(it)) {
                        TermUi.echo("Request failed. Reason: " + e.localizedMessage)
                    }
                    throw e;
                }
            }
        }
    }
}