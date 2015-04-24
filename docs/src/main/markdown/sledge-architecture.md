Sledge - Software Architecture Specification (SAS)
==================================================

Introduction and Goals
======================

Sledge is a Sling Application Manager which provides a simple user interface to easily deploy, uninstall, monitor and configure your applications in one step. 
Furthermore it provides a command line interface, best suited for remote management of your application in large enterprises. This allows for a nice integration into existing Continuous Delivery environments.

This document describes the architecture of the Sledge software based on the Apache Sling framework.

Requirements Overview
---------------------

The requirements are documented directly in the Story tasks.


Quality Goals
-------------

| Prio | Quality Goal        | Description                                           |
| -----|---------------------| ------------------------------------------------------|
| 1    | Maintainability     | A new developer shall be able to understand the concepts and the software in less than 2 hours. There shall be documentation for developers, operators and normal users. |
| 2    | Reliability / Robustness | The software shall handle errors during Application installation gracefully and display an understandable error message. |
| 3    | Portability         | The software can be used on Unix, Linux and Windows machines. |
| 4    | Performance         | An Application package installation shall not take longer than 2 minutes. |
| 5    | Flexibility         | The software shall offer a GUI and an API for remote command line usages. |
| 6    | Usability           | An administrator shall need less than 30 minutes to understand and use the software over GUI. |


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
- Use OSGi-based approach, no system-approach (Docker, etc.)


System Scope and Context
========================



External Interfaces
-------------------

Solution Strategy
=================

Building Block View
===================

Level 1
-------

The following diagram shows the main building blocks of the system and
their interdependencies:

\<insert overview diagram here\>

Comments regarding structure and interdependencies at Level 1:

### Building Block Name 1 (Black Box Description)

\<insert the building block’s black box template here\>

### Building Block Name 2 (Black Box Description)

\<insert the building block’s black box template here\>

### …

\<insert the building block’s black box template here\>

### Building Block Name n (Black Box Description)

\<insert the building block’s black box template here\>

### Open Issues

Level 2
-------

### Building Block Name 1 (White Box Description)

\<insert diagram of building block 1 here\>

#### Building Block Name 1.1 (Black Box Description)

#### Building Block Name 1.2 (Black Box Description)

Structure according to black box template

#### …

#### Building Block Name 1.n (Black Box Description)

#### Description of Relationships

#### Open Issues

### Building Block Name 2 (White Box Description)

…

\<insert diagram of building block 2 here\>

#### Building Block Name 2.1 (Black Box Description)

Structure according to black box template

#### Building Block Name 2.2 (Black Box Description)

#### …

#### Building Block Name 2.n (Black Box Description)

#### Description of Relationships

#### Open Issues

### Building Block Name 3 (White Box Description)

…

\<insert diagram of building block 3 here\>

#### Building Block Name 3.1 (Black Box Description)

#### Building Block Name 3.2 (Black Box Description)

#### …

#### Building Block Name 3.n (Black Box Description)

#### Description of Relationships

#### Open Issues

Level 3
-------

Runtime View
============

Runtime Scenario 1
------------------

Runtime Scenario 2
------------------

…
-

some more

Runtime Scenario n
------------------

Deployment View
===============

Infrastructure Level 1
----------------------

### Deployment Diagram Level 1

### Processor 1

\<insert node template here\>

### Processor 2

\<insert node template here\>

### …

### Processor n

\<insert node template here\>

### Channel 1

### Channel 2

### …

### Channel m

Infrastructure Level 2
----------------------

Concepts
========

Domain Models
-------------

Recurring or Generic Structures and Patterns
--------------------------------------------

### ecurring or Generic Structure 1

\<insert diagram and descriptions here\>

### Recurring or Generic Structure 2

\<insert diagram and descriptions here\>

Persistency
-----------

User Interface
--------------

Ergonomics
----------

Flow of Control
---------------

Transaction Procession
----------------------

Session Handling
----------------

Security
--------

Safety
------

Communications and Integration
------------------------------

Distribution
------------

Plausibility and Validity Checks
--------------------------------

Exception/Error Handling
------------------------

System Management and Administration
------------------------------------

Logging, Tracing
----------------

Business Rules
--------------

Configurability
---------------

Parallelization and Threading
-----------------------------

Internationalization
--------------------

Migration
---------

Testability
-----------

Scaling, Clustering
-------------------

High Availability
-----------------

Code Generation
---------------

Build-Management
----------------

Design Decisions
================

Decision Topic Template
-----------------------

Decision Topic 1
----------------

**Decision.**


Technical Risks
===============

Glossary
========

+--------------------+--------------------+-------------------------------------------------------------------+
| Glossary           |                    |                                                                   |
+====================+====================+===================================================================+
| Sling              | Apache Sling       | An Open Source web framework managed by the Apache community  |
+--------------------+--------------------+--------------------+
| Application Package | --            | An Application Package consists of a set of bundles and a Sledge Deployment Configuration file. |
+--------------------+--------------------+--------------------+


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
