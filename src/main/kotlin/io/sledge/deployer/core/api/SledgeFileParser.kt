package io.sledge.deployer.core.api

import java.io.File

interface SledgeFileParser {

    fun parseSledgeFile(sledgeFile: File): SledgeFile

}
