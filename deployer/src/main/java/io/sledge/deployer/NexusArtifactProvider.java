package io.sledge.deployer;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Fetches artifacts from a given Nexus repository.
 *
 * @see Unirest library
 */
public class NexusArtifactProvider {

    private static final Logger LOG = LoggerFactory.getLogger(NexusArtifactProvider.class);

    private String nexusBaseUrl;
    private String repositoryName;
    private String nexusUser;
    private String nexusUserPw;

    public NexusArtifactProvider(String nexusBaseUrl, String repositoryName) {
        this.nexusBaseUrl = nexusBaseUrl;
        this.repositoryName = repositoryName;
    }

    public NexusArtifactProvider(String nexusBaseUrl, String repositoryName, String nexusUser, String nexusUserPw) {
        this.nexusBaseUrl = nexusBaseUrl;
        this.repositoryName = repositoryName;
        this.nexusUser = nexusUser;
        this.nexusUserPw = nexusUserPw;
    }

    public InputStream fetch(String artifactId, String groupId, String type, String version) {
        return fetch(artifactId, groupId, type, version, "");
    }

    public InputStream fetch(String artifactId, String groupId, String type, String version, String classifier) {
        GetRequest request;
        HttpResponse<InputStream> response;

        LOG.info("Fetching {}:{}:{}:{}:{} from {} (repositoryName = {})....", artifactId, groupId, type, version, classifier, nexusBaseUrl, repositoryName);

        try {

            request = Unirest.get(nexusBaseUrl);

            if (isNotBlank(nexusUser)) {
                request = Unirest.get(nexusBaseUrl).basicAuth(nexusUser, nexusUserPw);
            }

            response = request
                    .queryString("r", repositoryName)
                    .queryString("g", groupId)
                    .queryString("a", artifactId)
                    .queryString("v", version)
                    .queryString("e", type)
                    .queryString("c", classifier)
                    .asBinary();
        } catch (UnirestException e) {
            throw new RuntimeException("Could not download the artifact from Nexus repository. ", e);
        }

        if (response.getStatus() > 400) {
            throw new RuntimeException(
                    "Could not download the artifact from Nexus repository: " + response.getStatusText() + " , Status code: " + response
                            .getStatus());
        }

        return response.getBody();
    }
}