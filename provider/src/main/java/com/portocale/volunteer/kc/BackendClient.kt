package com.portocale.volunteer.kc

import com.fasterxml.jackson.databind.ObjectMapper
import org.jboss.logging.Logger
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.Base64

/**
 * Talks to Volunteer_BE over HTTP using basic auth.
 * Uses the JDK HttpClient so no HTTP library is bundled into the jar.
 */
class BackendClient(
        baseUrl: String,
        username: String,
        password: String,
        timeoutSeconds: Int,
        ) {
    private val baseUrl: String = baseUrl.trimEnd('/')
    private val authHeader: String = "Basic " + Base64.getEncoder()
            .encodeToString("$username:$password".toByteArray(StandardCharsets.UTF_8))
    private val timeout: Duration = Duration.ofSeconds(timeoutSeconds.toLong())
    private val http: HttpClient = HttpClient.newBuilder()
            .connectTimeout(timeout)
        .build()

    /** GET /api/v1/users?email=... — null when the user does not exist. */
    fun getUserByEmail(email: String): VolunteerUser? {
        val encoded = URLEncoder.encode(email, StandardCharsets.UTF_8)
        val request = newRequest("/api/v1/users?email=$encoded").GET().build()

        val response = send(request, "getUserByEmail") ?: return null
        return when (response.statusCode()) {
            200 -> parseUser(response.body(), "getUserByEmail")
            404 -> null
            else -> {
                log.warnf("getUserByEmail: unexpected status %d", response.statusCode())
                null
            }
        }
    }

    /** GET /api/v1/users/{id} — null when the user does not exist. */
    fun getUserById(id: String): VolunteerUser? {
        val encoded = URLEncoder.encode(id, StandardCharsets.UTF_8)
        val request = newRequest("/api/v1/users/$encoded").GET().build()

        val response = send(request, "getUserById") ?: return null
        return when (response.statusCode()) {
            200 -> parseUser(response.body(), "getUserById")
            404 -> null
            else -> {
                log.warnf("getUserById: unexpected status %d", response.statusCode())
                null
            }
        }
    }

    /**
     * POST /api/v1/users/login — true when the password is correct.
     * 400 means wrong password, 404 means no such user. Anything else is
     * treated as a failure so a broken backend never lets someone in.
     */
    fun login(email: String, password: String): Boolean {
        val body = MAPPER.createObjectNode()
                .put("email", email)
                .put("password", password)
                .toString()

        val request = newRequest("/api/v1/users/login")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build()

        val response = send(request, "login") ?: return false
        return when (response.statusCode()) {
            200 -> true
            400, 404 -> false
            else -> {
                log.warnf("login: unexpected status %d", response.statusCode())
                false
            }
        }
    }

    private fun newRequest(path: String): HttpRequest.Builder =
            HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", authHeader)
            .header("Accept", "application/json")
            .timeout(timeout)

    private fun send(request: HttpRequest, operation: String): HttpResponse<String>? =
            try {
        http.send(request, HttpResponse.BodyHandlers.ofString())
    } catch (e: Exception) {
        log.error("$operation: backend call failed", e)
        null
    }

    private fun parseUser(body: String, operation: String): VolunteerUser? =
            try {
        MAPPER.readValue(body, VolunteerUser::class.java)
    } catch (e: Exception) {
        log.error("$operation: could not parse backend response", e)
        null
    }

    private companion object {
        private val log: Logger = Logger.getLogger(BackendClient::class.java)
        private val MAPPER = ObjectMapper()
    }
}