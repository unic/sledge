package io.sledge.deployer.core.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class InstallationDefinitionTest {

    @Test
    fun shouldReturnNameWhenInitializedWithNameAndEmptyList() {
        // Given
        val deploymentDefinition = InstallationDefinition("dev", emptyList())

        // When
        // Then
        assertEquals("dev", deploymentDefinition.name);
        assertNotNull(deploymentDefinition.artifacts)
    }
}
