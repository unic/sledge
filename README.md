Sledge - "One-Click Rollout"
============================

Sledge offers a set of scripts to easily deploy vault packages to an AEM instance.


# In bullet points

* “One-Click Rollouts”
* Simple command line tool
* Easy integration with CI/CD systems


# Requirements

* Kotlin 1.3.x
* Gradle 5.x


# Building from source

Build project:

`gradle build`

Create executable jar containing all needed dependencies:

`gradle shadowJar`


# Documentation

## The deployment-configuration.yaml file

See an example `deployment-configration.yaml` [here](src/main/resources/deployment-configuration.yaml).

Use this file to define the needed _deployment definitions_ for your application.

## Deployment commands

Install packages:

`java -jar io.sledge.deployer-<VERSION>-all.jar --user=XX --password=XX install <deploymentDefinitionName> <targetServer>`

Uninstall packages:

`java -jar io.sledge.deployer-<VERSION>-all.jar --user=XX --password=XX uninstall <deploymentDefinitionName> <targetServer>`

Use with custom retry configuration:

`java -jar io.sledge.deployer-<VERSION>-all.jar --retries=10 --retry-delay=2 --install-uninstall-wait-time=3 install <deploymentDefinitionName> <targetServer>`

See also the command line help to get the current valid options.

Display help:

`java -jar sledge-deployer-VERSION-all.jar -h`


# Release process

We use this Gradle release plugin here: https://github.com/researchgate/gradle-release

## Process

* Make sure you have committed and pushed everything properly
* Execute `gradle release`, the command line will guide you through the process
* Set the needed versions, use the Semantic Versioning approach
* Create a new release in Github and upload the generated binaries in the `build/libs` folder to the Github release
* Add the needed release notes and publish the Github release

You can then manually deploy the Sledge artifacts into your own repository for example with this command:

`mvn deploy:deploy-file -DgroupId=io.sledge -DartifactId=io.sledge.deployer -Dclassifier=all -DgeneratePom=true -Dversion=2.0.0-rc.1 -Dpackaging=jar -Dfile=io.sledge.deployer-2.0.0-rc.1-all.jar -DrepositoryId=myrepoId -Durl=https://my-repo`



# License

Sledge is licensed under the terms of the Apache License, version 2.0.

For the licenses of included products, see [NOTICE](NOTICE.txt)
