package ca.uwaterloo.cook_sharp.data.recipe_api
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

/**
 * Functionalities:
 * - Build request URLs from a path and query parameters
 * - Execute HTTP GET requests
 * - Return the raw response body as a string
 */
class ApiClient(
    private val apiKey: String = APIConfig.API_KEY,
    private val baseUrl: String = APIConfig.BASE_URL
) {
    fun get(
        path: String,
        queryParams: Map<String, String> = emptyMap()
    ): String {
        val allParams = queryParams + ("apiKey" to apiKey)

        val queryString = allParams.entries.joinToString("&") { (key, value) ->
            "${encode(key)}=${encode(value)}"
        }

        val url = URL("$baseUrl$path?$queryString")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("Accept", "application/json")
        }

        return try {
            val code = connection.responseCode
            val stream = if (code in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }

            if (code !in 200..299) {
                throw IllegalStateException("Recipe API request failed ($code): $body")
            }

            body
        } finally {
            connection.disconnect()
        }
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, Charsets.UTF_8.name())
}