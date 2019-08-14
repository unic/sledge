package io.sledge.deployer.http

import com.github.ajalt.clikt.output.TermUi.echo
import io.sledge.deployer.core.api.Configuration
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.Duration

class HttpClient(val configuration: Configuration) {

    val MEDIA_TYPE_ZIP = "application/zip".toMediaTypeOrNull()

    var client = OkHttpClient.Builder()

    fun postMultipart(url: String, parameters: Map<String, *>): Response {
        val request = createRequest(url, parameters)
        echo("Request url: " + request.url)
        return client.callTimeout(Duration.ofSeconds(configuration.timeout.toLong())).build().newCall(request).execute()
    }

    private fun createRequest(url: String, parameters: Map<String, *>): Request {
        return Request.Builder()
                .header("Authorization", Credentials.basic(configuration.user, configuration.password))
                .url(configuration.targetUrl + url)
                .post(createMultipartRequestBody(parameters))
                .build()
    }

    private fun createMultipartRequestBody(parameters: Map<String, *>): MultipartBody {
        var requestBody = MultipartBody.Builder().setType(MultipartBody.FORM);

        parameters.forEach { (key, value) ->
            when (value) {
                is String -> requestBody.addFormDataPart(key, value)
                is File -> requestBody.addFormDataPart(key, value.name, value.asRequestBody(MEDIA_TYPE_ZIP))

            }
        }

        return requestBody.build()
    }
}
