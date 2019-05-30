package io.sledge.deployer

import io.sledge.deployer.commands.SledgeCommand
import io.sledge.deployer.crx.CrxDeployer
import io.sledge.deployer.yaml.YamlSledgeFileParser

fun main(args: Array<String>) {
    val deployer = CrxDeployer()
    val sledgeFileParser = YamlSledgeFileParser()

    SledgeCommand(sledgeFileParser, deployer).main(args)
}
