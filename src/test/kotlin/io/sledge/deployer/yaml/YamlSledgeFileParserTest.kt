package io.sledge.deployer.yaml

import io.sledge.deployer.core.api.DeployerImplementation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

internal class YamlSledgeFileParserTest {

    val testee = YamlSledgeFileParser()
    val sledgeFile = File("src/test/resources/deployment-configuration.yaml")

    @Test
    fun parseSledgeFile() {
        // Given

        // When
        val result = testee.parseSledgeFile(sledgeFile)

        // Then
        assertNotNull(result)
        assertEquals(result.appName, "my-app")
        assertEquals(result.deployerImplementation, DeployerImplementation.crx)
        assertTrue(result.findDeploymentDefinitionByName("local-author") != null)
    }

    @Test
    fun shouldContainUninstallCleanupPaths() {
        // Given

        // When
        val result = testee.parseSledgeFile(sledgeFile)

        // Then
        assertTrue(result.uninstallCleanupPaths.size == 2)
    }
}