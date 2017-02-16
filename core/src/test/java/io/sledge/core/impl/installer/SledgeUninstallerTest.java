package io.sledge.core.impl.installer;

import io.sledge.core.api.ApplicationPackage;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.io.InputStream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SledgeUninstallerTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private ApplicationPackage applicationPackage;

    @Mock
    private Resource packageResource;

    @Mock
    private InputStream packageInputStream;

    @InjectMocks
    private SledgeUninstaller testee;

    @Test
    public void uninstall() throws Exception {
        // Given:
        //when(applicationPackage.getPackageFile()).thenReturn(packageInputStream);

        // TODO test

        // When:
        //testee.uninstall(applicationPackage);

        // Then:
        //verify(resourceResolver).delete(packageResource);
    }

}