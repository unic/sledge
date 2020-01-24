package io.sledge.deployer.checks

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.common.endOfRetries
import io.sledge.deployer.common.retry
import io.sledge.deployer.core.api.Configuration
import io.sledge.deployer.http.HttpClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.IOException

class BundleStatusCheck(val configuration: Configuration) {

    private val bundleStatusApiUrl = "${configuration.targetUrl}/system/console/bundles.json"

    fun executeCheck() {
        val httpClient = HttpClient(configuration)
        val json = Json(JsonConfiguration.Stable)
        val delayInMilliseconds = configuration.retryDelay * 1000L

        echo("\nWaiting for bundles starting up...")
        for (x in 1..configuration.initBundleStatusRetries) {
            val response = httpClient.doGetRequestWithAuth(bundleStatusApiUrl)
            if (!response.isSuccessful) throw IOException("Bundle status check has failed with: $response")
            val bundlesJsonResult = json.parse(BundlesJson.serializer(), response.bodayAsString)
            echo(bundlesJsonResult.status)
            Thread.sleep(configuration.initBundleStatusRetryDelay * 1000L)
        }

        echo("\nChecking bundle status...")
        runBlocking {
            retry(retries = configuration.retries, delay = delayInMilliseconds) {
                try {
                    val response = httpClient.doGetRequestWithAuth(bundleStatusApiUrl)

                    if (!response.isSuccessful) throw IOException("Bundle status check has failed with: $response")

                    val bundlesJsonResult = json.parse(BundlesJson.serializer(), response.bodayAsString)
                    val bundlesResolved = bundlesJsonResult.s[3]
                    val bundlesInstalled = bundlesJsonResult.s[4]

                    echo(bundlesJsonResult.status)

                    if (bundlesResolved > 0 || bundlesInstalled > 0) {
                        throw RuntimeException("Not all bundles are started yet successfully: ${bundlesJsonResult.status}\nPlease check in the log files!")
                    }
                } catch (e: Exception) {
                    if (endOfRetries(it)) {
                        echo("Bundle status check has failed. Reason: " + e.localizedMessage)
                    }
                    throw e;
                }
            }
        }
    }
}

/**
 * status = status text as string
 * s = array: (bundles existing, active, fragment, resolved, installed)
 */
@Serializable
data class BundlesJson(val status: String, val s: List<Int>, val data: List<BundleData>? = null)

@Serializable
data class BundleData(
        val id: Int,
        val name: String,
        val fragment: Boolean,
        val stateRaw: Int,
        val state: String,
        val version: String,
        val symbolicName: String,
        val category: String)
