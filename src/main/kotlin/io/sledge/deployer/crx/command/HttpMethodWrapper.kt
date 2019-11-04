package io.sledge.deployer.crx.command

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.common.retry
import io.sledge.deployer.exception.SledgeCommandException
import io.sledge.deployer.http.HttpClient
import kotlinx.coroutines.runBlocking

fun executePost(httpClient: HttpClient, command: Command, parameters: Map<String, Any> = emptyMap(), retries: Int, retryDelay: Int) {
    val delayInMilliseconds = retryDelay * 1000L

    runBlocking {
        retry(retries = retries, delay = delayInMilliseconds) {
            try {
                val params = mergeDefaultParameters(command, parameters)
                val response = httpClient.postMultipart(command.url(), params)
                command.validate(response)
            } catch (se: SledgeCommandException) {
                if (endOfRetries(it)) {
                    echo("Sledge request failed. Reason: " + se.localizedMessage)
                    echo("-----------------------------------------------")
                    echo("Error information:")
                    echo("Request url: ${se.requestUrl}")
                    echo("Response code: ${se.responseCode}")
                    echo("Response body: ${se.responseBody}")
                    echo("-----------------------------------------------\n")
                }
                throw se;
            } catch (e: Exception) {
                if (endOfRetries(it)) {
                    echo("Request failed. Reason: " + e.localizedMessage)
                }
                throw e;
            }
        }
    }
}

private fun endOfRetries(currentRetryCount: Long) = currentRetryCount == 0L

private fun mergeDefaultParameters(command: Command, parameters: Map<String, Any>): Map<String, Any?> {
    return mapOf("cmd" to command.cmdParam).plus(parameters)
}





