package io.sledge.core.impl.repository;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.mockito.Mockito.verify;


@Ignore
public class SledgeApplicationPackageSearchServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private ResourceResolver resourceResolver;

    @InjectMocks
    private SledgeApplicationPackageSearchService testee;

    @Test
    public void find() throws Exception {
        // Given:
        String groupId = "io.sledge";
        String artifactId = "sledge-test-app";
        String version = "1.0.0-SNAPSHOT";

        // When:
        List<Resource> applicationPackages = testee.find(groupId, artifactId, version);

        // Then:
        verify(resourceResolver).findResources("/jcr:root/etc/sledge/packages//*[@sling:resourceType='sledge/package' and @groupId='io.sledge' and @artifactId='sledge-test-app' and @version='1.0.0-SNAPSHOT']", "xpath");
    }

    @Test
    public void findOnlyWithGroupId() throws Exception {
        // Given:
        String groupId = "io.sledge";
        String artifactId = "";
        String version = "";

        // When:
        List<Resource> applicationPackages = testee.find(groupId, artifactId, version);

        // Then:
        verify(resourceResolver).findResources("/jcr:root/etc/sledge/packages//*[@sling:resourceType='sledge/package' and @groupId='io.sledge']", "xpath");
    }

    @Test
    public void findOnlyWithArtifactId() throws Exception {
        // Given:
        String groupId = null;
        String artifactId = "sledge-test-app";
        String version = "";

        // When:
        List<Resource> applicationPackages = testee.find(groupId, artifactId, version);

        // Then:
        verify(resourceResolver).findResources("/jcr:root/etc/sledge/packages//*[@sling:resourceType='sledge/package' and @artifactId='sledge-test-app']", "xpath");
    }

}