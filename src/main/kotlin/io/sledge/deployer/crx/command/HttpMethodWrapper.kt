package io.sledge.deployer.crx.command

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.common.retry
import io.sledge.deployer.exception.SledgeCommandException
import io.sledge.deployer.http.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Response

fun executePost(httpClient: HttpClient, command: Command, parameters: Map<String, Any> = emptyMap(), retries: Int) {
    runBlocking {
        delay(2000)
        retry(retries = retries) {
            try {
                executeRequest(command, parameters, httpClient)
            } catch (se: SledgeCommandException) {
                echo("Request failed. Reason: " + se.localizedMessage)
                throw se;
            } catch (e: Exception) {
                echo("Request failed. Reason: " + e.localizedMessage)
                throw e;
            }

        }
    }
}

private fun executeRequest(command: Command, parameters: Map<String, Any>, httpClient: HttpClient): Response {
    val url = createUrl(command)
    val params = mergeDefaultParameters(command, parameters)
    echo("-----------------------------------------------")
    echo("Command: " + command.commandName)
    echo("Parameters: $params")

    val response = httpClient.postMultipart(url, params)
    echo("-----------------------------------------------")

    command.validate(response)

    return response
}

private fun mergeDefaultParameters(command: Command, parameters: Map<String, Any>): Map<String, Any?> {
    return mapOf("cmd" to command.cmd).plus(parameters)
}

private fun createUrl(command: Command): String {
    return command.commandUrl + "?cmd=" + command.cmd
}





