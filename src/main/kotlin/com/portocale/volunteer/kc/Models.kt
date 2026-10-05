package com.portocale.volunteer.kc

import java.time.Instant
import java.util.stream.Stream
import org.keycloak.component.ComponentModel
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.SubjectCredentialManager
import org.keycloak.storage.StorageId
import org.keycloak.storage.adapter.AbstractUserAdapter

data class LoginRequest(
  val email: String,
  val password: String,
)

data class UpdatePasswordRequest(
  val userId: String,
  val currentPassword: String,
  val newPassword: String
)

data class ResetPasswordRequest(
  val email: String
)

/** What the BE returns from /api/v1/users. Only the fields Keycloak needs. */
data class VolunteerUser(
  val id: String,
  val firstName: String,
  val lastName: String,
  val email: String,
  val role: String,
  val status: UserStatus,
  val suspendedUntil: Instant? = null,
  val locale: String = "en",
  val forceResetPassword: Boolean = false
) {

  /**
   * INACTIVE means the registration OTP was never confirmed.
   * SUSPENDED blocks login until suspendedUntil has passed.
   */
  fun isLoginAllowed(): Boolean = when (status) {
    UserStatus.ACTIVE -> true
    UserStatus.INACTIVE -> false
    UserStatus.SUSPENDED -> suspendedUntil?.let {
      runCatching { it.isBefore(Instant.now()) }.getOrDefault(false)
    } ?: false
  }
}

enum class UserStatus {
  ACTIVE,
  INACTIVE,
  SUSPENDED,
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
  companion object {
    const val ROLE_ATTRIBUTE = "role"
    const val FORCE_RESET_PASSWORD = "forceResetPassword"
  }

  init {
    storageId = StorageId(model.id, user.id)
  }

  private val userAttributes = mapOf(
    USERNAME to listOf(user.email),
    EMAIL to listOf(user.email),
    FIRST_NAME to listOf(user.firstName),
    LAST_NAME to listOf(user.lastName),
    ROLE_ATTRIBUTE to listOf(user.role),
    LOCALE to listOf(user.locale),
    FORCE_RESET_PASSWORD to listOf(user.forceResetPassword.toString()),
  )

  override fun getUsername(): String = user.email

  override fun getEmail(): String = user.email

  override fun getFirstName(): String = user.firstName

  override fun getLastName(): String = user.lastName

  override fun isEmailVerified(): Boolean = user.status != UserStatus.INACTIVE

  /** Disabled users cannot log in: this is how INACTIVE and SUSPENDED are enforced. */
  override fun isEnabled(): Boolean = user.isLoginAllowed()

  /** No credentials are stored in Keycloak: the backend validates passwords. */
  override fun credentialManager(): SubjectCredentialManager =
    session.users().getUserCredentialManager(this)

  override fun getAttributeStream(name: String): Stream<String> =
    userAttributes[name]?.stream() ?: Stream.empty()

  override fun getAttributes(): Map<String, List<String>> =
    userAttributes

  override fun getFirstAttribute(name: String): String? {
    return userAttributes[name]?.get(0)
  }
}
