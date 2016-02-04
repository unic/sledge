Sledge - Core
===================

The Sledge Core module contains the main logic for handling the whole Application package installation, configuration and uninstallation.

Build project
-------------

Compile and build:

```
mvn install
```

Deploy bundle to a running Sling instance (http://localhost:8080):

```
mvn install -P autoInstallBundle
```