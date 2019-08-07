package io.sledge.deployer

import com.github.ajalt.clikt.core.subcommands
import io.sledge.deployer.commands.Install
import io.sledge.deployer.commands.SledgeCommand
import io.sledge.deployer.commands.Uninstall
import io.sledge.deployer.crx.CrxDeployer
import io.sledge.deployer.yaml.YamlSledgeFileParser

fun main(args: Array<String>) {
    val deployer = CrxDeployer()
    val sledgeFileParser = YamlSledgeFileParser()

    SledgeCommand().subcommands(Install(sledgeFileParser, deployer), Uninstall(sledgeFileParser, deployer)).main(args)
}
