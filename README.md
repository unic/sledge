[![Build Status](https://travis-ci.org/unic/sledge.svg?branch=develop)](https://travis-ci.org/unic/sledge)

Sledge - "One-Click Rollout"
============================

Sledge is a _Sling Application Manager_ which provides a simple user interface to easily deploy, uninstall, monitor and configure your applications in one step. 
Furthermore it provides a command line interface, best suited for remote management of your application in large enterprises. This allows for a nice integration into existing Continuous Delivery environments.

# In bullet points

* “One-Click Rollouts”
* “Application Controller/Manager”
* Concept of _Applications_ not _Packages_
  * An _Application_ may contain several packages (vault), bundles (osgi) and configurations
* “Configuration-Solution-agnostic”: No new/additional solution
* Based on standards: Sling-way to do stuff (OSGi Installer, CRUD API)

# Why Sledge?

Most Sling/AEM applications consists of a collection of several bundles and configurations. Sledge wants to support here and offers a concept of an [_Application Package_](https://github.com/unic/sledge/blob/develop/docs/src/main/markdown/sledge-architecture.md#applicationpackage) which includes all needed bundles and configurations for a specific environment. It simplifies the deployment of these bundles and the environment-specific configuration.

# What does Sledge do exactly?

Sledge is based completely on the [Apache Sling framework](https://sling.apache.org) (which is also heavily used in [_Adobe Experience Manager_](docs.adobe.com/docs/en/aem.html)) and leverages the functionalities provided by Sling, e.g. [_OSGi Installer_](https://sling.apache.org/documentation/bundles/osgi-installer.html), [_CRUD APIs_](https://sling.apache.org/documentation/the-sling-engine/sling-api-crud-support.html), etc.

Sledge's tasks are mainly: Managing _Application Packages_ and handling configurations replacement in the _Application Package_ before the deployment. 

Therefore Sledge is not a _special new_ Configuration mechanism for Sling but rather a "controller/manager/conductor" of _Application Packages_.

# Requirements

* Java 8
* Sling 8 or AEM 6.2
* Commons Lang 3.4 Osgi bundle


# Building from source

Sledge uses Maven 3.3.x.

Execute the following command from the root directory:

```
mvn clean install
```

# Using the _Sledge Launchpad_

This is the simplest and fastest way how to get started with Sledge and to check it out.

The _Sledge Launchpad_ module defines a simple [_Provisioning model_](https://sling.apache.org/documentation/development/slingstart.html) which
provides you with a full Sling 8 server and with all the needed _Sledge_ artifacts to run it properly.

* Build the project as mentioned above
* In the `laundpad` directory execute the following:
```
launchpad#> mvn slingstart:start -Dlaunchpad.keep.running=true
```

This configures and starts up a Sling 8 server with the _Sledge_ web application.

Access then the Sledge webapp here: http://localhost:8080/etc/sledge/packages.html

# Manual Sling Installation

* Install and run Sling 8 Launchpad
* Install manually the [Commons Lang 3.4 bundle](https://commons.apache.org/proper/commons-lang/download_lang.cgi)
* Install `core`, `connectors` and `webapp` packages, check the `deploy.sh` script for the Maven commands
* Open browser and enter this url: http://localhost:8080/etc/sledge/packages.html

The logged in user needs write permission to: `/apps/sledge_packages` and `/etc/sledge/packages` to make the application work properly.

# AEM installation

* Install the `io.sledge.delivery-VERSION-aem.zip` package
* Open url: http://localhost:4502/etc/sledge/packages.html

# Testing

## Unit tests

Sledge heavily uses Mockito and AssertJ for Unit Testing.

## Integration/Web Testing

Sledge uses the great [ScalaTest Selenium framework](http://www.scalatest.org/user_guide/using_selenium) for writing Integration/Web tests.

To run a full Integration test build just execute the following command:
```
root#> mvn clean install -P integration
```


# Documentation

## Architecture specification

See here: [Sledge Architecture](docs/src/main/markdown/sledge-architecture.md)


# Release process

* Make sure your _develop_ branch is up-to-date and does not contain any local changes
* Execute locally the ```release.sh``` script
* Adapt the `sledge.txt` Launchpad model to reflect the next development version and commit
* Finally push everything to GitHub remote
* Create a new release on GitHub
* Add some release description
* Upload the following artifacts for the release:
  * delivery
  * deployer
* Publish the release


# License

Sledge is licensed under the terms of the Apache License, version 2.0.

For the licenses of included products, see [NOTICE](NOTICE.txt)
