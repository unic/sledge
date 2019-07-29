package io.sledge.deployer.crx.command

import com.github.ajalt.clikt.output.TermUi
import io.sledge.deployer.common.Retry
import io.sledge.deployer.exception.SledgeCommandException
import io.sledge.deployer.http.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

const val PATH = "/crx/packmgr/service"
const val SERVICE_JSP = "$PATH.jsp"

data class Command(val commandName: String, val cmd: String, val commandUrl: String, val validationTexts: Set<String>) {
     fun validate(responseBody: String) { if(validationTexts.any{it in responseBody}) true else throw SledgeCommandException("Response did not contain expected validation text")
     }
}

val Install = Command(commandName = "Install", cmd = "inst", commandUrl = SERVICE_JSP, validationTexts = setOf("Package installed"))
val Uninstall = Command(commandName = "Unistall", cmd = "uninst", commandUrl = SERVICE_JSP,validationTexts =  setOf("Package uninstalled"))
val Remove = Command(commandName = "Remove", cmd = "rm", commandUrl = SERVICE_JSP,validationTexts =  setOf("200", "does not exist"))
val Upload = Command(commandName = "Upload", cmd = "upload", commandUrl = SERVICE_JSP, validationTexts = setOf("200"))

fun executePost(httpClient: HttpClient, command: Command, packageName: String, parameter: Pair<String, *> = Pair("", "")) {
    runBlocking {
        delay(2000)
        Retry().retry {
            try {
                val url = command.commandUrl + "?cmd=" + command.cmd
                val parameters = mapOf("cmd" to command.cmd, "recursive" to "true", parameter.first to parameter.second, "name" to packageName)
                TermUi.echo("-----------------------------------------------")
                TermUi.echo("Action: " + command.commandName + " / package: " + packageName)
                val response = httpClient.postMultipart(url, parameters)
                command.validate(response.body?.string() ?: "")
                TermUi.echo("Response status code: " + response.code)

            } catch (se: SledgeCommandException){
                TermUi.echo("Request failed. Reason " + se.localizedMessage)
                throw se;
            }
            catch (e: Exception) {
                TermUi.echo("Request failed. Reason " + e.localizedMessage)
                throw e;
            }

        }
    }
}





