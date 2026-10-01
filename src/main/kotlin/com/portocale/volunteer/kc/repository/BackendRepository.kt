package com.portocale.volunteer.kc.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.portocale.volunteer.kc.LoginRequest
import com.portocale.volunteer.kc.VolunteerUser
import java.util.concurrent.TimeUnit
import okhttp3.Credentials
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.http.HttpHeaders
import org.jboss.logging.Logger

private const val DEFAULT_TIMEOUT = 60L

class BackendRepository(
  baseUrl: String,
  username: String,
  password: String
) {

  private val baseUrl = baseUrl.trimEnd('/')

  private val client =
    OkHttpClient.Builder()
      .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
      .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
      .callTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
      .addInterceptor { chain ->
        val request =
          chain.request()
            .newBuilder()
            .header(HttpHeaders.AUTHORIZATION, Credentials.basic(username, password))
            .header(HttpHeaders.ACCEPT, "application/json")
            .build()

        chain.proceed(request)
      }
      .build()

  fun getUserByEmail(email: String): VolunteerUser? {
    val url =
      "$baseUrl/api/v1/users"
        .toHttpUrl()
        .newBuilder()
        .addQueryParameter("email", email)
        .build()

    val request =
      Request.Builder()
        .url(url)
        .get()
        .build()

    return executeUserRequest(request, "getUserByEmail")
  }

  fun getUserById(id: String): VolunteerUser? {
    val url =
      "$baseUrl/api/v1/users"
        .toHttpUrl()
        .newBuilder()
        .addPathSegment(id)
        .build()

    val request =
      Request.Builder()
        .url(url)
        .get()
        .build()

    return executeUserRequest(request, "getUserById")
  }

  fun login(
    email: String,
    password: String,
  ): Boolean {
    val body =
      MAPPER.writeValueAsString(
        LoginRequest(
          email = email,
          password = password,
        ),
      )

    val request =
      Request.Builder()
        .url("$baseUrl/api/v1/users/login")
        .post(
          body.toRequestBody(JSON),
        )
        .build()

    return try {
      client.newCall(request).execute().use { response ->
        when (response.code) {
          200 -> true
          400, 404 -> false

          else -> {
            log.warnf(
              "login: unexpected status %d",
              response.code,
            )
            false
          }
        }
      }
    } catch (e: Exception) {
      log.error("login: backend call failed", e)
      false
    }
  }

  private fun executeUserRequest(
    request: Request,
    operation: String,
  ): VolunteerUser? =
    try {
      client.newCall(request).execute().use { response ->
        when (response.code) {
          200 -> response.body.string().let {
            MAPPER.readValue(
              it,
              VolunteerUser::class.java,
            )
          }

          404 -> null

          else -> {
            log.warnf(
              "$operation: unexpected status %d",
              response.code,
            )
            null
          }
        }
      }
    } catch (e: Exception) {
      log.error("$operation: backend call failed", e)
      null
    }

  private companion object {
    private val log =
      Logger.getLogger(BackendRepository::class.java)

    private val MAPPER: ObjectMapper =
      jacksonObjectMapper()

    private val JSON =
      "application/json; charset=utf-8".toMediaType()
  }
}
