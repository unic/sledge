package io.sledge.deployer.crx.command

import io.sledge.deployer.exception.SledgeCommandException


const val PATH = "/crx/packmgr/service"
const val SERVICE_JSP = "$PATH.jsp"

data class Command(val commandName: String, val cmd: String, val commandUrl: String, val responseValidationConditions: Set<String>) {
    fun validate(responseBody: String) {
        if (responseValidationConditions.any { it in responseBody }) true else throw SledgeCommandException("Response did not contain expected validation text")
    }
}

val Install = Command(commandName = "Install", cmd = "inst", commandUrl = SERVICE_JSP, responseValidationConditions = setOf("Package installed"))
val Uninstall = Command(commandName = "Unistall", cmd = "uninst", commandUrl = SERVICE_JSP, responseValidationConditions = setOf("Package uninstalled"))
val Remove = Command(commandName = "Remove", cmd = "rm", commandUrl = SERVICE_JSP, responseValidationConditions = setOf("200", "does not exist"))
val Upload = Command(commandName = "Upload", cmd = "upload", commandUrl = SERVICE_JSP, responseValidationConditions = setOf("200"))

