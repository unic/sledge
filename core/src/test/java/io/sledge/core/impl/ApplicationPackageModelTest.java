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

import io.sledge.core.api.models.ApplicationPackageModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fest.reflect.core.Reflection.field;

/**
 * @author oliver.burkhalter
 */
public class ApplicationPackageModelTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    private String packageFilename = "com.test.webapp.zip";

    @InjectMocks
    private ApplicationPackageModel testee;

    @Before
    public void setup() {
        field("packageFilename").ofType(String.class).in(testee).set(packageFilename);
    }

    @Test
    public void testGetPackageFilename() {
        assertThat(testee.getPackageFilename()).isEqualTo(packageFilename);
    }
}
