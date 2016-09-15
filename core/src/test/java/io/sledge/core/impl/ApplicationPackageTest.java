/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
