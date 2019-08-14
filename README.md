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

TODO

Example commands

Install packages:

`java -jar sledge-deployer-VERSION-all.jar --user=XX --password=XX install deploymentDefinitionName targetServer`

Uninstall packages:

`java -jar sledge-deployer-VERSION-all.jar --user=XX --password=XX uninstall deploymentDefinitionName targetServer`

Get the help:

`java -jar sledge-deployer-VERSION-all.jar -h`


# Release process

TODO

Still manual process.


# License

Sledge is licensed under the terms of the Apache License, version 2.0.

For the licenses of included products, see [NOTICE](NOTICE.txt)
