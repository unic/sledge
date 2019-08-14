package io.sledge.deployer.core.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class DeploymentDefinitionTest {

    @Test
    fun shouldReturnNameWhenInitializedWithNameAndEmptyList() {
        // Given
        val deploymentDefinition = DeploymentDefinition("dev", emptyList())

        // When
        // Then
        assertEquals("dev", deploymentDefinition.name);
        assertNotNull(deploymentDefinition.artifacts)
    }
}
