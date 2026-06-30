package ir.kenar.core.network

import ir.kenar.data.session.SessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
    private val http: OkHttpClient,
    private val sessionStore: SessionStore,
) {
    // آدرس سرور — بعداً از BuildConfig می‌خونیم
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun get(path: String, authenticated: Boolean = true): JSONObject =
        execute(Request.Builder().url(baseUrl + path).get(), authenticated)

    suspend fun post(
        path: String,
        body: JSONObject = JSONObject(),
        authenticated: Boolean = true,
    ): JSONObject = execute(
        Request.Builder()
            .url(baseUrl + path)
            .post(body.toString().toRequestBody(JSON_TYPE)),
        authenticated,
    )

    private suspend fun execute(builder: Request.Builder, authenticated: Boolean): JSONObject {
        builder.header("Accept-Language", "fa")
        if (authenticated) {
            val token = sessionStore.currentToken() ?: throw ApiException.Unauthenticated
            builder.header("Authorization", "Bearer $token")
        }

        val response = try {
            withContext(Dispatchers.IO) { http.newCall(builder.build()).execute() }
        } catch (e: IOException) {
            throw ApiException.Network(e)
        }

        return response.use { resp ->
            val text = resp.body?.string().orEmpty()
            val json = runCatching { JSONObject(text) }.getOrDefault(JSONObject())
            if (!resp.isSuccessful) {
                val msg = json.optString("error").ifBlank { null }
                throw ApiException.Server(resp.code, msg)
            }
            json
        }
    }

    companion object {
        private val JSON_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}