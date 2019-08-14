package io.sledge.deployer.yaml

import com.charleskorn.kaml.Yaml
import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.core.api.Artifact
import io.sledge.deployer.core.api.DeploymentDefinition
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
                mapDeyplomentDefinitionsWithArtifactsPathPrefix(result.artifactsPathPrefix, result.deploymentDefs))
    }

    private fun mapDeyplomentDefinitionsWithArtifactsPathPrefix(artifactsPathPrefix: String, deploymentDefList: List<YamlDeploymentDef>): List<DeploymentDefinition> {
        return deploymentDefList.map { installationDefItem ->
            DeploymentDefinition(installationDefItem.name, installationDefItem.artifacts.map { a ->
                Artifact("$artifactsPathPrefix$a")
            })
        }
    }
}

@Serializable
data class YamlSledgeRoot(
        val artifactsPathPrefix: String,
        val deploymentDefs: List<YamlDeploymentDef>
)

@Serializable
data class YamlDeploymentDef(
        val name: String,
        val artifacts: List<String>
)
