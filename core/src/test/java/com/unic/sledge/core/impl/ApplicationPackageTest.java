package com.unic.sledge.core.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fest.reflect.core.Reflection.field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.unic.sledge.core.api.models.ApplicationPackage;

/**
 * @author oliver.burkhalter
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationPackageTest {

    private String artifactId = "com.test.webapp";

    @InjectMocks
    private ApplicationPackage testee;

    @Before
    public void setup() {
        field("artifactId").ofType(String.class).in(testee).set(artifactId);
    }

    @Test
    public void getArtifactId() {
        assertThat(testee.getArtifactId()).isEqualTo(artifactId);
    }
}
