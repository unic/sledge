package io.sledge.deployer.yaml

import io.sledge.deployer.core.api.DeployerImplementation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

internal class YamlSledgeFileParserTest {

    @Test
    fun parseSledgeFile() {
        // Given
        val testee = YamlSledgeFileParser()
        val sledgeFile = File("src/test/resources/deployment-configuration.yaml")

        // When
        val result = testee.parseSledgeFile(sledgeFile)

        // Then
        assertNotNull(result)
        assertEquals(result.deploymentName, "my-app")
        assertEquals(result.deployerImplementation, DeployerImplementation.crx)
        assertTrue(result.findDeploymentDefinitionByName("local-author") != null)
    }
}