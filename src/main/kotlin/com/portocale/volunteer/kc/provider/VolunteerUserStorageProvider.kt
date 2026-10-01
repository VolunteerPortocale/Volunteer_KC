package com.portocale.volunteer.kc.provider

import com.portocale.volunteer.kc.VolunteerUserAdapter
import com.portocale.volunteer.kc.repository.BackendRepository
import org.keycloak.component.ComponentModel
import org.keycloak.credential.CredentialInput
import org.keycloak.credential.CredentialInputValidator
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.UserCredentialModel
import org.keycloak.models.UserModel
import org.keycloak.models.credential.PasswordCredentialModel
import org.keycloak.storage.StorageId
import org.keycloak.storage.UserStorageProvider
import org.keycloak.storage.user.UserLookupProvider

/**
 * Looks users up in Volunteer_BE and asks it to check passwords.
 * Keycloak stores no credentials for these users.
 */
class VolunteerUserStorageProvider(
  private val session: KeycloakSession,
  private val model: ComponentModel,
  private val backend: BackendRepository,
) : UserStorageProvider, UserLookupProvider, CredentialInputValidator {

  override fun getUserById(realm: RealmModel, id: String): UserModel? {
    val externalId = StorageId.externalId(id)
    val user = backend.getUserById(externalId) ?: return null
    return VolunteerUserAdapter(session, realm, model, user)
  }

  /** Usernames are email addresses in this system. */
  override fun getUserByUsername(realm: RealmModel, username: String): UserModel? =
    getUserByEmail(realm, username)

  override fun getUserByEmail(realm: RealmModel, email: String): UserModel? {
    val user = backend.getUserByEmail(email) ?: return null
    return VolunteerUserAdapter(session, realm, model, user)
  }

  override fun supportsCredentialType(credentialType: String): Boolean =
    PasswordCredentialModel.TYPE == credentialType

  override fun isConfiguredFor(realm: RealmModel, user: UserModel, credentialType: String): Boolean =
    supportsCredentialType(credentialType)

  override fun isValid(realm: RealmModel, user: UserModel, input: CredentialInput): Boolean {

    if (!supportsCredentialType(input.type) || input !is UserCredentialModel) {
      return false
    }
    val password = input.challengeResponse
    if (password.isNullOrEmpty()) {
      return false
    }
    val email = user.email ?: return false
    return backend.login(email, password)
  }

  override fun close() {
    // nothing to release
  }
}
