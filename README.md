Sledge - "One-Click Rollout"
============================

Sledge offers a set of scripts to easily deploy vault packages to an AEM instance.


# In bullet points

* “One-Click Rollouts”
* Simple command line tool
* Easy integration with CI/CD systems


# Requirements

* Kotlin 1.3
* Gradle 5.x


# Building from source

`gradle build`

Create executable jar containing all needed dependencies:

`gradle shadowJar`


# Documentation

## The deployment-configuration.yaml file

See an example `deployment-configration.yaml` [here](src/main/resources/deployment-configuration.yaml).

Use this file to define the needed _deployment definitions_ for your application.

## Deployment commands

Install packages:

`java -jar sledge-deployer-VERSION-all.jar --user=XX --password=XX install deploymentDefinitionName targetServer`

Uninstall packages:

`java -jar sledge-deployer-VERSION-all.jar --user=XX --password=XX uninstall deploymentDefinitionName targetServer`

Get the help:

`java -jar sledge-deployer-VERSION-all.jar -h`


# Release process

We use this Gradle release plugin here: https://github.com/researchgate/gradle-release

## Process

* Make sure you have committed and pushed everything properly
* Execute `gradle release`, the command line will guide you through the process
* Set the needed versions, use the Semantic Versioning approach
* Create a new release in Github and upload the generated binaries in the `build/libs` folder to the Github release
* Add the needed release notes and publish the Github release


# License

Sledge is licensed under the terms of the Apache License, version 2.0.

For the licenses of included products, see [NOTICE](NOTICE.txt)
