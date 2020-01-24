package io.sledge.deployer

import com.github.ajalt.clikt.core.subcommands
import io.sledge.deployer.console.Install
import io.sledge.deployer.console.SledgeCommand
import io.sledge.deployer.console.Uninstall
import io.sledge.deployer.yaml.YamlSledgeFileParser

fun main(args: Array<String>) {
    val sledgeFileParser = YamlSledgeFileParser()
    SledgeCommand().subcommands(Install(sledgeFileParser), Uninstall(sledgeFileParser)).main(args)
}
