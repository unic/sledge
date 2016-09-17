import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest

getDeliveryPackageFromNexus = { deployConfig ->
    println "Downloading package from Nexus Repository ${deployConfig.nexusRepositoryBaseUrl}..."
    HttpResponse<InputStream> binaryResponse = Unirest.get(deployConfig.nexusRepositoryBaseUrl)
            .queryString("r", deployConfig.nexusRepositoryName)
            .queryString("g", deployConfig.groupId)
            .queryString("a", deployConfig.artifactId)
            .queryString("v", deployConfig.version)
            .queryString("e", deployConfig.packageType)
            .queryString("c", deployConfig.classifier)
            .asBinary()

    if (binaryResponse.status > 400) {
        throw new RuntimeException("FAILED dowloading delivery package: ${binaryResponse.status} - ${binaryResponse.statusText}")
    }

    return binaryResponse.body
}