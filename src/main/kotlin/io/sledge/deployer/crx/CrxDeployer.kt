package io.sledge.deployer.crx

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.core.api.Deployer
import io.sledge.deployer.core.api.Deployment
import khttp.extensions.fileLike
import khttp.post
import java.io.File

class CrxDeployer : Deployer {

    override fun deploy(deployment: Deployment) {
        echo("Start deployment for ${deployment.deploymentDef.name}")
        for (artifact in deployment.deploymentDef.deploymentArtifacts) {
            echo("Deploying artifact: ${artifact.filePath}...")

            // Upload: curl -u admin:admin -F cmd=upload -F force=true -F package=@test.zip http://localhost:4502/crx/packmgr/service/.json

            val url = "http://httpbin.org/post"
            val files = listOf(File(artifact.filePath).fileLike())

            val r = post(url, files = files)

            echo("${r.text}")

            // Install: curl -u admin:admin -F cmd=install http://localhost:4502/crx/packmgr/service/.json/etc/packages/my_packages/test.zip

            // Uninstall: curl -u admin:admin -F cmd=uninstall http://localhost:4502/crx/packmgr/service/.json/etc/packages/my_packages/test.zip

            // Delete: curl -u admin:admin -F cmd=delete http://localhost:4502/crx/packmgr/service/.json/etc/packages/my_packages/test.zip
        }
    }
}
