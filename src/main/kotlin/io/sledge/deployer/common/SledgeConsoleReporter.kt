package io.sledge.deployer.common

import com.github.ajalt.clikt.output.TermUi
import io.sledge.deployer.core.exception.SledgeCommandException

class SledgeConsoleReporter {

    fun writeSledgeCommandExceptionInfo(se: SledgeCommandException) {
        TermUi.echo("Sledge request failed. Reason: " + se.localizedMessage)
        TermUi.echo("-----------------------------------------------")
        TermUi.echo("Error information:")
        TermUi.echo("Request url: ${se.requestUrl}")
        TermUi.echo("Response code: ${se.responseCode}")
        TermUi.echo("Response body: ${se.responseBody}")
        TermUi.echo("-----------------------------------------------\n")
    }
}