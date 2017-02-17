import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import io.sledge.deployer.SledgeDeployer


def defaultDeployConfig = [
        environmentName       : "",
        environmentFileContent: "",

        targetHost            : "http://localhost:4502",
        targetHostUsername    : "admin",
        targetHostPassword    : "admin"
]

//
// Handle command line options and release configurations
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

def releaseConfigObject = new ConfigSlurper().parse(new File("release-def.groovy").toURI().toURL())
def sledgeDeployer = new SledgeDeployer(deployConfig.targetHost, deployConfig.targetHostUsername, deployConfig.targetHostPassword);

//
// Start deployment process
//

if (!deployConfig.environmentName) {
    printMessage("Required 'environmentName' option is missing.")
    System.exit(1)
}

// Only execute Sledge commands if Sledge is installed properly
checkAndWaitForSledgeApp(sledgeDeployer, 5, 1000)

// Uninstallation
releaseConfigObject.packages.each { key, value ->

    def packageList = searchPackages(sledgeDeployer, 20, value.groupId, value.artifactId, value.version);

    if (packageList.size() == 1 && !value.forceUpdate) {
        printMessage("Package <${value.artifactId}> does exist already in same version and won't be installed (use forceUpdate to overwrite this behaviour).")
    } else {
        def packageFilename = "${value.artifactId}-${value.version}${value.classifier ? '-' + value.classifier : ''}.${value.type}"

        printMessage("Handling uninstallation for package file: ${packageFilename}...")

        // Uninstall all related packages
        def uninstallSucceeded = uninstallApp(sledgeDeployer, value.groupId, value.artifactId, "")

        if (uninstallSucceeded) {
            removeApp(sledgeDeployer, value.groupId, value.artifactId, "")
        } else {
            printMessage("*** Stopping Installation process, there were Uninstallation problems. Please check the errors with your Admin and retry execution. ***")
            System.exit(1)
        }

        checkAndWaitForSledgeApp(sledgeDeployer, 20, 3000)
    }
}

// Wait for proper AEM state
checkAndWaitForBundles(sledgeDeployer, 5, 4000)
checkAndWaitForSledgeApp(sledgeDeployer, 20, 3000)

printMessage("Uninstallation has been successful\nStarting now with the installation of packages...")

// Installation
releaseConfigObject.packages.each { key, value ->

    def packageList = searchPackages(sledgeDeployer, 20, value.groupId, value.artifactId, value.version);

    if (packageList.size() == 1 && !value.forceUpdate) {
        printMessage("Skip Package installation of <${value.artifactId}> because it is already installed.")
    } else {
        def packageFilename = "${value.artifactId}-${value.version}${value.classifier ? '-' + value.classifier : ''}.${value.type}"
        File packageFile = new File("${releaseConfigObject.packagesPath}/${packageFilename}")

        printMessage("Handling installation for package file: ${packageFilename}...")

        uploadApp(sledgeDeployer, packageFile, value.groupId, value.artifactId, value.version, 10)
        installApp(sledgeDeployer, packageFilename, deployConfig.environmentName, deployConfig.environmentFileContent)

        checkAndWaitForSledgeApp(sledgeDeployer, 10, 3000)
    }
}

checkAndWaitForSledgeApp(sledgeDeployer, 30, 3000)
checkAndWaitForBundles(sledgeDeployer, 12, 5000)


printMessage("Deployment has been finished successfully!")


Unirest.shutdown();

//
// End deployment process
//

def printMessage(text) {
    println ""
    println "***"
    println "${text}"
    println "***"
    println ""
}

def checkAndWaitForSledgeApp(sledgeDeployer, maxCount, waitTimeInMs) {
    def checkCount = 0
    def status = 500

    println ""
    print "Checking Sledge Application availability..."

    while (checkCount == 1 || (status >= 400 && checkCount <= maxCount)) {
        def response = sledgeDeployer.checkSledgeStatus()
        status = response.status
        checkCount++
        sleep(waitTimeInMs)

        print ".."
    }

    if (status >= 400) {
        println ""
        println "Stopping installation: Sledge Application is not available."
        println "Please check your instance and restart it, if needed."
        println ""
        System.exit(1)
    }

    print " OK"
    println ""
}


def checkAndWaitForBundles(sledgeDeployer, maxCount, waitTimeInMs) {
    def checkCount = 0
    def bundlesResolved = 1
    def bundlesInstalled = 1

    println ""
    print "Checking Felix bundles status..."

    while ((checkCount < 2 || bundlesResolved > 0 || bundlesInstalled > 0) && checkCount <= maxCount) {
        def bundlesResponse = sledgeDeployer.checkActiveBundles();

        bundlesInstalled = bundlesResponse.installed
        bundlesResolved = bundlesResponse.resolved
        checkCount++

        if (checkCount > 1 && bundlesInstalled == 0 && bundlesResolved == 0) {
            println ""
            println "All bundles are active and running."
            continue
        }

        if (checkCount > maxCount) {
            println ""
            println "*** Check failed: Felix Container is not ready, bundles are not all in Active state."
            println "*** Restart your instance and check again."
            println ""
            System.exit(1)
        }

        sleep(waitTimeInMs)
        print ".."
    }

    println ""
}

def searchPackages(sledgeDeployer, maxRetryCount, groupId, artifactId, version) {
    def resultList = null
    def retryCount = 0

    while (resultList == null && retryCount <= maxRetryCount) {
        try {
            resultList = sledgeDeployer.searchPackages(groupId, artifactId, version)
            retryCount++
        } catch (e) {
            retryCount++
            sleep(3000)
        }
    }

    if (resultList == null) {
        printMessage("Could not search for packages, AEM/Sling instance is not properly available.\nPlease check the Felix console and check with your Admin.")
        System.exit(1)
    }

    return resultList;
}

def uploadApp(SledgeDeployer sledgeDeployer, File packageFile, groupId, artifactId, version, maxRetryCount) {
    HttpResponse<String> uploadResponse = sledgeDeployer.uploadApp(packageFile, groupId, artifactId, version)

    def retryCount = 0
    while (uploadResponse.status >= 400 && retryCount < maxRetryCount) {
        println "Upload HTTP status: ${uploadResponse.status} / ${uploadResponse.statusText}"
        println "Uploading application has failed, Retrying...${retryCount}/${maxRetryCount}\n"
        sleep(3000)
        uploadResponse = sledgeDeployer.uploadApp(packageFile, groupId, artifactId, version)
        retryCount++
    }

    if (uploadResponse.status >= 400) {
        println uploadResponse.body
        println "Upload has failed.\n"
        System.exit(1)
    } else {
        println "Upload successfully finished."
    }
}

def installApp(SledgeDeployer sledgeDeployer, deliveryPackageName, environment, environmentFileContent) {
    HttpResponse<String> response = sledgeDeployer.installApp(deliveryPackageName, environment, environmentFileContent)

    if (response.status >= 400) {
        println "Install HTTP status: ${response.status}"
        println response.body
        println "Installation has failed. Please check the errors and check with your Admin."
        System.exit(1)
    } else {
        println "Installation successfully finished."
    }
}

def uninstallApp(SledgeDeployer sledgeDeployer, groupId, artifactId, version) {
    HttpResponse<String> uninstallResponse = sledgeDeployer.uninstallApp(groupId, artifactId, version)

    if (uninstallResponse.status >= 400) {
        println "Uninstall HTTP status: ${uninstallResponse.status}"
        println uninstallResponse.body
        println "Uninstallation has failed."
        return false
    } else {
        println "Uninstallation successfully finished."
        return true
    }
}

def removeApp(SledgeDeployer sledgeDeployer, groupId, artifactId, version) {
    HttpResponse<String> response = sledgeDeployer.removeApp(groupId, artifactId, version)

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

    if (response.status == 404 || response.status == 403) {
        println "Resource ${resourcePath} was not available or operation was forbidden on this resource."
    }

    if (response.status < 400) {
        println "Resource Removal successfully finished."
    }
}