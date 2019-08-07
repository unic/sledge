package io.sledge.deployer.yaml

import com.charleskorn.kaml.Yaml
import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.core.api.Artifact
import io.sledge.deployer.core.api.InstallationDefinition
import io.sledge.deployer.core.api.SledgeFile
import io.sledge.deployer.core.api.SledgeFileParser
import kotlinx.serialization.Serializable
import java.io.File

class YamlSledgeFileParser : SledgeFileParser {

    override fun parseSledgeFile(sledgeFile: File): SledgeFile {
        echo("Parsing ${sledgeFile.name}...")

        val result = Yaml.default.parse(YamlSledgeRoot.serializer(), sledgeFile.inputStream().readBytes().toString(Charsets.UTF_8))

        return SledgeFile(
                result.artifactsPathPrefix,
                result.uninstallationDefs,
                mapInstallationDefinitionsWithArtifactsPathPrefix(result.artifactsPathPrefix, result.installationDefs))
    }

    private fun mapInstallationDefinitionsWithArtifactsPathPrefix(artifactsPathPrefix: String, installationDefList: List<YamlInstallationDef>): List<InstallationDefinition> {
        return installationDefList.map { installationDefItem ->
            InstallationDefinition(installationDefItem.name, installationDefItem.artifacts.map { a ->
                Artifact("$artifactsPathPrefix$a")
            })
        }
    }
}

@Serializable
data class YamlSledgeRoot(
        val artifactsPathPrefix: String,
        val uninstallationDefs: List<String>,
        val installationDefs: List<YamlInstallationDef>
)

@Serializable
data class YamlInstallationDef(
        val name: String,
        val artifacts: List<String>
)
