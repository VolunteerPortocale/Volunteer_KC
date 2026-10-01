package com.portocale.volunteer.kc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant
import java.util.stream.Stream
import org.keycloak.component.ComponentModel
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.RoleModel
import org.keycloak.models.SubjectCredentialManager
import org.keycloak.storage.StorageId
import org.keycloak.storage.adapter.AbstractUserAdapter

data class LoginRequest(
    val email: String,
    val password: String,
)

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


/**
 * Presents a Volunteer_BE user as a Keycloak UserModel.
 * Read-only: users are managed in the app, not here.
 */
class VolunteerUserAdapter(
    session: KeycloakSession,
    realm: RealmModel,
    model: ComponentModel,
    private val user: VolunteerUser,
) : AbstractUserAdapter(session, realm, model) {

    init {
        storageId = StorageId(model.id, user.id)
    }

    override fun getUsername(): String? = user.email

    override fun getEmail(): String? = user.email

    override fun getFirstName(): String? = user.firstName

    override fun getLastName(): String? = user.lastName

    override fun isEmailVerified(): Boolean = user.status != "INACTIVE"

    /** Disabled users cannot log in: this is how INACTIVE and SUSPENDED are enforced. */
    override fun isEnabled(): Boolean = user.isLoginAllowed()

    /** Maps the single BE role onto a realm role of the same name, if it exists. */
    override fun getRoleMappingsInternal(): Set<RoleModel> {
        val roleName = user.role ?: return emptySet()
        val role = realm.getRole(roleName) ?: return emptySet()
        return setOf(role)
    }

    /** No credentials are stored in Keycloak: the backend validates passwords. */
    override fun credentialManager(): SubjectCredentialManager =
        session.users().getUserCredentialManager(this)

    override fun getAttributeStream(name: String): Stream<String> = when (name) {
        USERNAME, EMAIL -> user.email.asStream()
        FIRST_NAME -> user.firstName.asStream()
        LAST_NAME -> user.lastName.asStream()
        else -> Stream.empty()
    }
    override fun getAttributes(): Map<String, List<String>> = buildMap {
        user.email?.let {
            put(USERNAME, listOf(it))
            put(EMAIL, listOf(it))
        }
        user.firstName?.let { put(FIRST_NAME, listOf(it)) }
        user.lastName?.let { put(LAST_NAME, listOf(it)) }
    }
    override fun getFirstAttribute(name: String): String? =
        getAttributeStream(name).findFirst().orElse(null)

    private fun String?.asStream(): Stream<String> =
        if (this == null) Stream.empty() else Stream.of(this)
}