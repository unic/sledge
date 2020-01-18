package io.sledge.deployer.checks

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.common.retry
import io.sledge.deployer.core.api.Configuration
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class BundlesCheck(val configuration: Configuration) {

    private val bundlesJsonPath = "/system/console/bundles.json"

    fun executeCheck() {
        val httpClient = OkHttpClient()

        val request = Request.Builder()
                .header("Authorization", Credentials.basic(configuration.user, configuration.password))
                .url("${configuration.targetUrl}${bundlesJsonPath}")
                .build()

        val delayInMilliseconds = configuration.retryDelay * 1000L

        runBlocking {
            retry(retries = configuration.retries, delay = delayInMilliseconds) {
                try {
                    httpClient.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code: $response")

                        val json = Json(JsonConfiguration.Stable)
                        val bundlesJsonResult = json.parse(BundlesJson.serializer(), response.body!!.string())

                        val bundlesResolved = bundlesJsonResult.s[3]
                        val bundlesInstalled = bundlesJsonResult.s[4]

                        echo(bundlesJsonResult.status)

                        if (bundlesResolved > 0 || bundlesInstalled > 0) {
                            throw RuntimeException("Not all bundles are started successfully: ${bundlesJsonResult.status}\nPlease check in the log files!")
                        }
                    }
                } catch (e: Exception) {
                    if (endOfRetries(it)) {
                        echo("Bundles check failed. Reason: " + e.localizedMessage)
                    }
                    throw e;
                }
            }
        }
    }

    private fun endOfRetries(currentRetryCount: Long) = currentRetryCount == 0L
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
