package io.sledge.core.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fest.reflect.core.Reflection.field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import io.sledge.core.api.models.ApplicationPackage;

/**
 * @author oliver.burkhalter
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationPackageTest {

    private String packageFilename = "com.test.webapp.zip";

    @InjectMocks
    private ApplicationPackage testee;

    @Before
    public void setup() {
        field("packageFilename").ofType(String.class).in(testee).set(packageFilename);
    }

    @Test
    public void testGetPackageFilename() {
        assertThat(testee.getPackageFilename()).isEqualTo(packageFilename);
    }
}
