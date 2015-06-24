Sledge - Software Architecture Specification (SAS)
==================================================

Introduction and Goals
======================

Sledge is a _Sling Application Manager_ which provides a simple user interface to easily deploy, uninstall, monitor and configure your applications in one step. 
Furthermore it provides a command line interface, best suited for remote management of your application in large enterprises. This allows for a nice integration into existing _Continuous Delivery_ environments.

This document describes the architecture of the Sledge software based on the Apache Sling framework.

Requirements Overview
---------------------

The requirements are documented directly in the Story tasks.


Business Goals
--------------

### Decreased Time-to-Market of applications

Sledge simplifies the deployment of your AEM application such it enables you to do "One-click Rollouts". This enables you to push new features of your application to testing or production environments at the click of a button. This eliminates manual installation procedures as well as human error and allows to speed up the process manifold. Needless to say, money saved on not manually executing installation procedures can be spent on business functionality, which is far more important.

### Standardized application deployments

Sledge defines a standardized application package format which increases the maintainability of your applications and helps operation teams to execute Application deployments in a standardized way. This allows for a clean documentation and easy transferral of work items throughout the team.

### Centralized configuration

Sledge handles all application configurations in one place which decreases the time of configuration management of the operation teams. It also ensures quality, as configuration can be easily audited and reviewed.

### Customizable and Integration-friendly

Sledge provides connectors which enables you to attach Application package sources of your choice. This means you can install directly your Application packages from your FTP server or your Nexus repository. Additionally Sledge offers a remote service API which helps you to easily integrate with your Continuous Integration systems.


Quality Goals
-------------

| Prio | Quality Goal        | Description                                           |
| -----|---------------------| ------------------------------------------------------|
| 1    | Maintainability     | A new developer shall be able to understand the concepts and the software in less than 2 hours. There shall be documentation for developers, operators and normal users. |
| 2    | Reliability / Robustness | The software shall handle errors during Application installation gracefully and display an understandable error message. |
| 3    | Portability         | The software can be used on Unix, Linux and Windows machines. |
| 4    | Performance         | An Application package installation shall not take longer than 2 minutes. |
| 5    | Flexibility         | The software shall offer a GUI and an API for remote command line usages. |


Stakeholders
------------

| Role               | Description/Goal/Intention                                              |
|--------------------|-------------------------------------------------------------------------|
| Management         | Investment for innovation                                               |
| Technology Manager | Improvement of the software development process                         |
| Developer          | Fast and automated deployment of applications                           |
| Operator/Admin     | Stable rollouts of applications                                         |


Architecture Constraints
========================

Technical Constraints
---------------------
- Use Apache Sling as base framework
- Use OSGi-based approach, no system-approach (Docker, etc.). This gives us a more flexibility regarding to the implementation with the customer.


System Scope and Context
========================

![Sledge system scope][context-diagram]

[context-diagram]: ../plantuml-generated/context-diagram.png "Sledge System scope"

- Admin: The Admin user installs and uninstalls Application Packages via the WebappUI or the remote API.
- Package source: A Package source provides Application packages for the installation. That may be a local path in the current repository or in some other external systems like FTP, Adobe Package Manager, Nexus repository, etc.


Solution Strategy
=================

We have chosen the Apache Sling web framework as our base framework because the solution shall be mainly available for the AEM platform which is also based on the Apache Sling web framework. The implementation focus on an OSGi-based deployment model instead of using a system approach model like doing such automation via Docker, etc. The reason why we decided to do so is because there are situations where the IT departement has not the possibility to change easily to such deployment technologies.


Building Block View
===================

Level 1
-------

The following diagram shows the main building blocks of the system and
their interdependencies:

![Building block level1][building-block-level1-diagram]

[building-block-level1-diagram]: ../plantuml-generated/building-block-level1-diagram.png "Sledge System - Building block Level 1"


### sledge-webapp

The webapp is responsible for rendering and handling the user interface.

### sledge-connectors

The Package sources are provided by Connectors. For each type of Package source there is a Connector package.

### sledge-core

The core package handles all relevant tasks for Application package installation and uninstallation. The installation and configuration can be defined by a Sledge deployment file.

### Apache Sling

The Apache Sling web framework.


sledge-core package
-------------------

![Building blocks sledge-core][building-block-core-diagram]

[building-block-core-diagram]: ../plantuml-generated/building-block-core-diagram.png "Sledge Core - Building blocks"

### ApplicationPackage

The ApplicationPackage provides methods for getting data out of a Application package provided by a Package source connector:

- General information: version, groupId, artifactId, description, etc.
- List of contents

### PackageSourceConnector

The PackageSourceConnector defines methods for loading Application packages from a specific source location (local repository, Adobe Package Manager, Nexus repository, etc.). It returns a list of ApplicationPackage objects.

### DeploymentConfiguration

The DeploymentConfiguration is the central configuration for the installation of an Application package. It defines all needed information for deploying properly an application:

- Deployment locations for bundles
- Configurations for bundles


sledge-connectors package
-------------------------

TODO
![Building blocks sledge-connectors][building-block-connectors-diagram]

[building-block-connectors-diagram]: ../plantuml-generated/building-block-connectors-diagram.png "Sledge Connectors - Building blocks"


sledge-webapp package
---------------------

TODO
![Building blocks sledge-webapp][building-block-webapp-diagram]

[building-block-webapp-diagram]: ../plantuml-generated/building-block-webapp-diagram.png "Sledge Webapp - Building blocks"



Concepts
========

Domain Models
-------------

Persistency
-----------

User Interface
--------------

Session Handling
----------------

Security
--------

Plausibility and Validity Checks
--------------------------------

Exception/Error Handling
------------------------

System Management and Administration
------------------------------------

Logging, Tracing
----------------

Configurability
---------------

Testability
-----------

Code Generation
---------------

Build-Management
----------------


Technical Risks
===============

Glossary
========


| Keyword               | Description                                                          |
|---------------------|-------------------------------------------------------------------------|
| Apache Sling        | An Open Source web framework managed by the Apache community          |
| Application Package | An Application Package consists of a set of bundles and a Sledge Deployment Configuration file. |
| Bundle              | Defines an OSGi bundle, which is simply a jar with special Manifest directives. |



About arc42
===========

arc42, the Template for documentation of software and system
architecture.

By Dr. Gernot Starke, Dr. Peter Hruschka and contributors.

Template Revision: 6.5 EN (asciidoc-basiert), Juni 2014

© We acknowledge that this document uses material from the arc 42
architecture template, <http://www.arc42.de>. Created by Dr. Peter
Hruschka & Dr. Gernot Starke. For additional contributors see
<http://arc42.de/sonstiges/contributors.html>
