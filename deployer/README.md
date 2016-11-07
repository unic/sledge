Sledge Deployer Toolset
============================

The Sledge Deployer Toolset provides small tools to easily integrate into existing CI environments.
It provides common functions like uploading, installing and uninstalling Sledge applications. But it also offers some general functions
like removing nodes via the _SlingPostServlet_.

# Usage examples

In ```src/main/groovy``` you can see some example on how to use the ```SledgeDeployer``` in a Groovy script. It also gives an example
on how to download a package from a Nexus repository and install it via the ```SledgeDeployer``` and how to handle options passed
to the Groovy script for overwriting default deploy configurations.

With the following command you can call your groovy script and pass the needed options:

```
groovy -cp "lib/*" deploy.groovy -DartifactId=foo.bar -DgroupId=foo.bar.group -DpackageType=zip -Dversion=${version} -DuninstallVersion=${uninstallVersion} -DnexusRepositoryName=${repoName} -DenvironmentName=${environmentName} -DenvironmentFileContent=${environmentFileContent} -DtargetHostUsername=admin -DtargetHostPassword=${targetHostPassword} ${targetHost}
```

Replace the "${...}" variables with your correct value.

The "lib" directory is used to hold all needed dependencies.

Here is an example of a `deploy.groovy` call:

```
groovy -cp "lib/*" deploy.groovy -DartifactId=test-app-package -DgroupId=io.sledge.tester-packages -DpackageType=zip -Dversion=0.3.0 -DuninstallVersion=0.3.0 -DnexusRepositoryName=my-nexusrepoName -DnexusRepositoryBaseUrl=https://nexus.host.com/nexus/service/local/artifact/maven/redirect -DnexusUser=my-nexususr -DnexusUserPw=my-nexuspw -DenvironmentName=test-author -DenvironmentFileContent=TESTCONFIG=Foo -DtargetHostUsername=admin -DtargetHostPassword=admin http://localhost:8080
```

with the `nexusUser` and `nexusUserPw` you can set needed credentials data if your Nexus repository requires an Authentication.


To use it in an own project it is needed to build some _deployment_ package/module with all the needed dependencies:

```xml
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


