[![Build Status](https://travis-ci.org/unic/sledge.svg?branch=develop)](https://travis-ci.org/unic/sledge)

Sledge - "One-Click Rollout"
============================

Sledge is a Sling Application Manager which provides a simple user interface to easily deploy, uninstall, monitor and configure your applications in one step. 
Furthermore it provides a command line interface, best suited for remote management of your application in large enterprises. This allows for a nice integration into existing Continuous Delivery environments.


# Requirements

* Java 8
* Sling 8 or AEM 6.2
* Commons Lang 3.4 Osgi bundle


# Building from source

Sledge is built by Maven 3.3.x.

Execute the following command from the root directory:

```
mvn clean install
```


# Sling Installation

* Install and run Sling 8
* Install manually the [Commons Lang 3.4 bundle](https://commons.apache.org/proper/commons-lang/download_lang.cgi)
* Install `core`, `connectors` and `webapp` packages, check the `deploy.sh` script for the Maven commands
* Open browser and enter this url: http://localhost:8080/etc/sledge/packages.html

The logged in user needs write permission to: `/apps/sledge_packages` and `/etc/sledge/packages` to make the application work properly.


# AEM installation

* Install the `io.sledge.delivery-VERSION-aem.zip` package
* Open url: http://localhost:4502/etc/sledge/packages.html


# Documentation

## Architecture specification

See here: [Sledge Architecture](docs/src/main/markdown/sledge-architecture.md)


# License

Sledge is licensed under the terms of the Apache License, version 2.0.

For the licenses of included products, see [NOTICE](NOTICE.txt)
