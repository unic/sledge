package io.sledge.deployer.crx.command

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.exception.SledgeCommandException
import okhttp3.Response


const val PATH = "/crx/packmgr/service"
const val SERVICE_JSP = "$PATH.jsp"

data class Command(val commandName: String, val cmd: String, val commandUrl: String, val responseValidationConditions: Set<String>) {
    fun validate(response: Response) {
        val responseBody = response.body?.string() ?: ""
        if (responseValidationConditions.any { it in responseBody }) else {
            echo("-----------------------------------------------")
            echo("Error information:")
            echo("Response code: ${response.code}")
            echo("Response body: $responseBody")
            echo("-----------------------------------------------\n")
            throw SledgeCommandException("Response did not contain expected validation text.")
        }
    }
}

val Upload = Command(commandName = "Upload", cmd = "upload", commandUrl = SERVICE_JSP, responseValidationConditions = setOf("200"))
val Install = Command(commandName = "Install", cmd = "inst", commandUrl = SERVICE_JSP, responseValidationConditions = setOf("Package installed"))
val Uninstall = Command(commandName = "Uninstall", cmd = "uninst", commandUrl = SERVICE_JSP, responseValidationConditions = setOf("Package uninstalled", "does not exist"))
val Delete = Command(commandName = "Delete", cmd = "rm", commandUrl = SERVICE_JSP, responseValidationConditions = setOf("200", "does not exist"))

