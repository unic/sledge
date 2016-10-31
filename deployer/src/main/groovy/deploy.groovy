import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import io.sledge.deployer.SledgeDeployer
import io.sledge.deployer.NexusArtifactProvider
import org.apache.commons.io.FileUtils


def defaultDeployConfig = [
        artifactId            : "",
        groupId               : "",
        version               : "",
        uninstallVersion      : "",
        packageType           : "",
        classifier            : "",

        // or: snapshots or releases
        nexusRepositoryName   : "releases",
        nexusRepositoryBaseUrl: "",

        environmentName       : "",
        environmentFileContent: "",

        targetHost            : "http://localhost:4502",
        targetHostUsername    : "admin",
        targetHostPassword    : "admin"
]

//
// Handle command line options
//

def cli = new CliBuilder(usage: 'deploy.groovy [options] <targetHost>', header: 'Options:')
cli.h(longOpt: 'help', 'Print this message')
cli.D(args: 2, valueSeparator: '=', argName: 'property=value', 'Use value for given property')

// Workaround for invocation with gmavenplus-plugin, found no way to pass 'args' properly
if (args instanceof String) {
    args = args.split(",")
}

def options = cli.parse(args)

if (!options || !args || !options.arguments() || options.h) {
    cli.usage()
    return
}

Properties dOptionsMap = options.getInner().getOptionProperties("D")

// Set given targetHost
dOptionsMap.put("targetHost", options.arguments()[0])

// Merge default deploy configs with given deploy configs
def deployConfig = defaultDeployConfig << dOptionsMap

//
// Define common variables
//

def deliveryPackageFilename = "${deployConfig.artifactId}-${deployConfig.version}.${deployConfig.packageType}"
def uninstallDeliveryPackageFilename = "${deployConfig.artifactId}-${deployConfig.uninstallVersion}.${deployConfig.packageType}"

// Get delivery package
NexusArtifactProvider nexusArtifactProvider = new NexusArtifactProvider(deployConfig.nexusRepositoryBaseUrl, deployConfig.nexusRepositoryName)
InputStream packageStream = nexusArtifactProvider.fetch(deployConfig.artifactId, deployConfig.groupId, deployConfig.packageType, deployConfig.version, deployConfig.classifier)
File deliveryPackageFile = new File(deliveryPackageFilename)
FileUtils.copyInputStreamToFile(packageStream, deliveryPackageFile)

def sledgeDeployer = new SledgeDeployer(deployConfig.targetHost, deployConfig.targetHostUsername, deployConfig.targetHostPassword);

//
// Start deployment process
//

// If application exists, then uninstall and remove it first
if (resourceExists("${deployConfig.targetHost}${SledgeDeployer.SLEDGE_BASE_PATH}/${uninstallDeliveryPackageFilename}.html", deployConfig)) {
    uninstallApp(sledgeDeployer, uninstallDeliveryPackageFilename)
    removeApp(sledgeDeployer, uninstallDeliveryPackageFilename)
}

// Cleanup the artifacts in the CRX Package Manager
if ("" != deployConfig.groupId) {
    removeResource(sledgeDeployer, "/etc/packages/${deployConfig.groupId}")
}

// Cleanup app specific nodes
//removeResource(sledgeDeployer, "/apps/my-app")

// Wait and check Felix bundle status...
sledgeDeployer.checkActiveBundles(3, 5000)

uploadApp(sledgeDeployer, deliveryPackageFile)
installApp(sledgeDeployer, deliveryPackageFilename, deployConfig.environmentName, deployConfig.environmentFileContent)

// Wait and check Felix bundle status...
sledgeDeployer.checkActiveBundles(5, 5000)

Unirest.shutdown();

//
// End deployment process
//


def resourceExists(requestUrl, deployConfig) {
    HttpResponse<String> availableResponse = Unirest.get(requestUrl)
            .basicAuth(deployConfig.targetHostUsername, deployConfig.targetHostPassword)
            .asString()

    println "Resource exists? ${requestUrl} -> ${availableResponse.status} / ${availableResponse.statusText}"
    return availableResponse.status < 400
}

def uploadApp(SledgeDeployer sledgeDeployer, File packageFile) {
    HttpResponse<String> uploadResponse = sledgeDeployer.uploadApp(packageFile)

    if (uploadResponse.status == 401 || uploadResponse.status >= 500) {
        println "Upload HTTP status: ${uploadResponse.status}"
        println uploadResponse.body
        println "Upload has failed."
    } else {
        println "Upload successfully finished."
    }
}

def installApp(SledgeDeployer sledgeDeployer, deliveryPackageName, environment, environmentFileContent) {
    HttpResponse<String> response = sledgeDeployer.installApp(deliveryPackageName, environment, environmentFileContent)

    if (response.status >= 400) {
        println "Install HTTP status: ${response.status}"
        println response.body
        println "Installation has failed."
    } else {
        println "Installation successfully finished."
    }
}

def uninstallApp(SledgeDeployer sledgeDeployer, deliveryPackageName) {
    HttpResponse<String> uninstallResponse = sledgeDeployer.uninstallApp(deliveryPackageName)

    if (uninstallResponse.status >= 400) {
        println "Uninstall HTTP status: ${uninstallResponse.status}"
        println uninstallResponse.body
        println "Uninstallation has failed."
    } else {
        println "Uninstallation successfully finished."
    }
}

def removeApp(SledgeDeployer sledgeDeployer, deliveryPackageName) {
    HttpResponse<String> response  = sledgeDeployer.removeApp(deliveryPackageName)

    if (response.status >= 400) {
        println "Remove HTTP status: ${response.status}"
        println response.body
        println "Package Removal has failed."
    } else {
        println "Package Removal successfully finished."
    }
}

def removeResource(SledgeDeployer sledgeDeployer, resourcePath) {
    HttpResponse<String> response = sledgeDeployer.removeResource(resourcePath)

    if (response.status >= 500) {
        println "Remove HTTP status: ${response.status}"
        println response.body
        println "Resource Removal has failed."
    }

    if(response.status == 404 || response.status == 403) {
        println "Resource ${resourcePath} was not available or operation was forbidden on this resource."
    }

    if(response.status < 400) {
        println "Resource Removal successfully finished."
    }
}