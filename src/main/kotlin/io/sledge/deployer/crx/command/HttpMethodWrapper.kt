package io.sledge.deployer.crx.command

import com.github.ajalt.clikt.output.TermUi
import io.sledge.deployer.common.retry
import io.sledge.deployer.exception.SledgeCommandException
import io.sledge.deployer.http.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Response

fun executePost(httpClient: HttpClient, command: Command, packageName: String, retries: Long, parameter: Pair<String, *> = Pair("", "")) {
    runBlocking {
        delay(2000)
        retry(retries = retries) {
            try {
                val response = executeRequest(command, parameter, packageName, httpClient)
                TermUi.echo("Response http status " + response.code)
            } catch (se: SledgeCommandException) {
                TermUi.echo("Request failed. Reason " + se.localizedMessage)
                throw se;
            } catch (e: Exception) {
                TermUi.echo("Request failed. Reason " + e.localizedMessage)
                throw e;
            }

        }
    }
}

private fun executeRequest(command: Command, parameter: Pair<String, *>, packageName: String, httpClient: HttpClient): Response {
    val url = createUrl(command)
    val parameters = createParameters(command, parameter, packageName)
    TermUi.echo("-----------------------------------------------")
    TermUi.echo("Action: " + command.commandName + " / package: " + packageName)
    val response = httpClient.postMultipart(url, parameters)
    command.validate(response.body?.string() ?: "")
    return response
}

private fun createParameters(command: Command, parameter: Pair<String, *>, packageName: String): Map<String, Any?> {
    return mapOf("cmd" to command.cmd, "recursive" to "true", parameter.first to parameter.second, "name" to packageName)
}

private fun createUrl(command: Command): String {
    return command.commandUrl + "?cmd=" + command.cmd
}





