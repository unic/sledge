package io.sledge.deployer.http

import io.sledge.deployer.core.api.Configuration
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.Duration

/**
 * Provides a central HttpClient implementation for the Sledge requests.
 * This allows us to easily switch the underlying http client implementation.
 */
class HttpClient(val configuration: Configuration) {

    private val mediaTypeZip = "application/zip".toMediaTypeOrNull()

    private val httpClient = OkHttpClient.Builder().callTimeout(Duration.ofSeconds(configuration.callTimeout.toLong())).build()

    fun doGetRequestWithAuth(url: String): SledgeHttpResponse {
        val request = Request.Builder()
                .header("Authorization", Credentials.basic(configuration.user, configuration.password))
                .url(url)
                .build()

        return httpClient.newCall(request).execute().use { response ->
            SledgeHttpResponse(response.isSuccessful, response.code, response.body!!.string())
        }
    }

    fun postMultipart(path: String, parameters: Map<String, *>): SledgeHttpResponse {
        val request = Request.Builder()
                .header("Authorization", Credentials.basic(configuration.user, configuration.password))
                .url(configuration.targetUrl + path)
                .post(createMultipartRequestBody(parameters))
                .build()

        return httpClient.newCall(request).execute().use { response ->
            SledgeHttpResponse(response.isSuccessful, response.code, response.body!!.string())
        }
    }

    private fun createMultipartRequestBody(parameters: Map<String, *>): MultipartBody {
        var requestBody = MultipartBody.Builder().setType(MultipartBody.FORM);

        parameters.forEach { (key, value) ->
            when (value) {
                is String -> requestBody.addFormDataPart(key, value)
                is File -> requestBody.addFormDataPart(key, value.name, value.asRequestBody(mediaTypeZip))
            }
        }

        return requestBody.build()
    }
}
