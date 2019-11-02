package io.sledge.deployer.crx.command

import io.sledge.deployer.exception.SledgeCommandException
import okhttp3.Response

const val PATH = "/crx/packmgr/service"
const val SERVICE_JSP = "$PATH.jsp"

data class Command(val commandName: String, val cmdParam: String, val responseValidationConditions: Set<String>) {
    fun validate(response: Response) {
        val responseBody = response.body?.string() ?: ""
        if (responseValidationConditions.any { it in responseBody }) else {
            throw SledgeCommandException("Response did not contain expected validation text.", url(), response.code, responseBody)
        }
    }

    fun url(): String {
        return "$SERVICE_JSP?cmd=$cmdParam"
    }
}

val Upload = Command(commandName = "Upload", cmdParam = "upload", responseValidationConditions = setOf("200"))
val Install = Command(commandName = "Install", cmdParam = "inst", responseValidationConditions = setOf("Package installed"))
val Uninstall = Command(commandName = "Uninstall", cmdParam = "uninst", responseValidationConditions = setOf("Package uninstalled", "does not exist"))
val Delete = Command(commandName = "Delete", cmdParam = "rm", responseValidationConditions = setOf("200", "does not exist"))

