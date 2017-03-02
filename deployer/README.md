Sledge Deployer Toolset
============================

The Sledge Deployer Toolset provides small tools to easily integrate into existing CI environments.
It provides common functions like uploading, installing and uninstalling Sledge applications. But it also offers some general functions
like removing nodes via the _SlingPostServlet_.

The idea is to build up some standard scripts for AEM/Sling app deployments.

# Prerequisite

- Groovy 2.4.x
- Running AEM/Sling

**Why Groovy?**

We use currently Groovy scripts because it offers more flexibility to handle responses, for example JSON responses and it runs on the JVM.

# Artifacts

The deployer module generates two artifacts:

* deployer.jar: Includes a compiled version of the deploy.groovy script and all the SledgeDeployer classes
* deployer-all.zip: Packs together the jar artifact and the needed external libraries in a `lib/` folder. This is usually used for generating a complete delivery package for the customer in which
the `deploy`script is executable by an installed JRE (without Groovy compilation).


# Usage examples

In ```src/main/groovy``` you can see some example on how to use the ```SledgeDeployer``` in a Groovy script. It also gives an example on how to handle options passed
to the Groovy script for overwriting default deploy configurations. We use here the Groovy `CliBuilder`.

You can use the `deploy.groovy` and adapt it to your needs.

The current version handles automatically uninstallation and installation of packages.

It uses a `release-def.groovy` config file for defining all needed packages to install.

Example:

```groovy
packagesPath = "packages"

packages {
    mainApp {
        groupId = "my.group.id"
        artifactId = "my-group-artifact"
        version = "${project.version}"
        type = "zip"
        classifier = "sledge"
        forceUpdate = true
    }
    somelib {
        groupId = "com.some.lib"
        artifactId = "some-lib-artifact"
        version = "1.1.0"
        type = "jar"
        classifier = "foo"
    }
    someOtherlib {
        groupId = "com.someOtherlib.group"
        artifactId = "some-other-lib-artifact"
        version = "1.0.0"
        type = "jar"
        classifier = ""
    }
}
```

With `forceUpdate` you can define that a package should always be updated although it is already installed in the same version, else it will be skipped.

The `packagesPath` defines the location where the packages are stored. In the example above they are below a `packages` folder.

To use it in an own project you can simply add this dependency to your own _deployment/delivery_ module:

```xml
<dependency>
    <groupId>io.sledge</groupId>
    <artifactId>io.sledge.deployer</artifactId>
    <version>VERSION</version>
    <type>zip</type>
    <classifier>all</classifier>
</dependency>
```

and unpack it and use the `lib/` folder to get the needed libraries for the `deploy.groovy` script execution.

This can done for example very easily with the Maven Assembly Plugin.

## Artifacts from Nexus

If you want to download the packages from Nexus you can use the `NexusArtifactProvider` class.

The current `deploy.groovy` would be needed to be updated.

