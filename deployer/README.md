Sledge Deployer Toolset
============================

The Sledge Deployer Toolset provides small tools to easily integrate into existing CI environments.
It provides common functions like uploading, installing and uninstalling Sledge applications. But it also offers some general functions
like removing nodes via the _SlingPostServlet_.

# Usage examples

In ```src/main/groovy``` you can see some example on how to use the ```SledgeDeployer``` in a Groovy script. It also gives an example
on how to download a package from a Nexus repository and install it via the ```SledgeDeployer``` and how to handle options passed
to the Groovy script for overwriting default deploy configurations.

To use it in an own project it is needed to build some _deployment_ package/module with all the needed dependencies:

```
<dependency>
	<groupId>io.sledge</groupId>
	<artifactId>io.sledge.deployer</artifactId>
	<version>${sledge.version}</version>
</dependency>
<dependency>
	<groupId>com.mashape.unirest</groupId>
	<artifactId>unirest-java</artifactId>
	<version>1.4.9</version>
</dependency>
<dependency>
	<groupId>org.codehaus.groovy</groupId>
	<artifactId>groovy-json</artifactId>
	<version>2.4.7</version>
</dependency>
<dependency>
	<groupId>org.apache.httpcomponents</groupId>
	<artifactId>httpcore</artifactId>
	<version>4.4.4</version>
	<scope>compile</scope>
</dependency>
<dependency>
	<groupId>org.apache.httpcomponents</groupId>
	<artifactId>httpclient</artifactId>
	<version>4.5.2</version>
	<scope>compile</scope>
</dependency>
<dependency>
	<groupId>org.apache.httpcomponents</groupId>
	<artifactId>httpcore-nio</artifactId>
	<version>4.4.4</version>
	<scope>compile</scope>
</dependency>
<dependency>
	<groupId>org.apache.httpcomponents</groupId>
	<artifactId>httpmime</artifactId>
	<version>4.3.6</version>
	<scope>compile</scope>
</dependency>
<dependency>
	<groupId>commons-codec</groupId>
	<artifactId>commons-codec</artifactId>
	<version>1.9</version>
	<scope>compile</scope>
</dependency>
<dependency>
	<groupId>commons-io</groupId>
	<artifactId>commons-io</artifactId>
	<version>2.5</version>
	<scope>compile</scope>
</dependency>
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-lang3</artifactId>
	<version>3.4</version>
	<scope>compile</scope>
</dependency>
<dependency>
	<groupId>org.slf4j</groupId>
	<artifactId>slf4j-api</artifactId>
	<version>1.7.21</version>
	<scope>compile</scope>
</dependency>
<dependency>
	<groupId>org.slf4j</groupId>
	<artifactId>slf4j-simple</artifactId>
	<version>1.7.21</version>
	<scope>compile</scope>
</dependency>
```


