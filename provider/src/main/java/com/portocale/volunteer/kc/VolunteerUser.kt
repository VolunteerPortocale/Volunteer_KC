package com.portocale.volunteer.kc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant

/** What the BE returns from /api/v1/users. Only the fields Keycloak needs. */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VolunteerUser(
        var id: String? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        var email: String? = null,
        var role: String? = null,
        var status: String? = null,
        var suspendedUntil: String? = null,
        ) {
    /**
     * INACTIVE means the registration OTP was never confirmed.
     * SUSPENDED blocks login until suspendedUntil has passed.
     */
    fun isLoginAllowed(): Boolean = when (status) {
        "ACTIVE" -> true
        "SUSPENDED" -> suspendedUntil?.let {
            runCatching { Instant.parse(it).isBefore(Instant.now()) }.getOrDefault(false)
        } ?: false
        else -> false
    }
}