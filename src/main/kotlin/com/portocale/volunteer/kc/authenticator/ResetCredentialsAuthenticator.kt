package com.portocale.volunteer.kc.authenticator

import com.portocale.volunteer.kc.UpdatePasswordRequest
import com.portocale.volunteer.kc.VolunteerUserAdapter.Companion.FORCE_RESET_PASSWORD
import com.portocale.volunteer.kc.repository.BackendRepository
import org.keycloak.authentication.AuthenticationFlowContext
import org.keycloak.authentication.Authenticator
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.UserModel
import org.keycloak.models.utils.FormMessage
import org.keycloak.storage.StorageId

private const val RESET_CREDENTIALS_FORM_TPL = "reset_credentials.ftl"

class ResetCredentialsAuthenticator(
  private val repository: BackendRepository
) : Authenticator {

  override fun authenticate(context: AuthenticationFlowContext) {
    val user = context.user
    val forceResetPassword = user.getFirstAttribute(FORCE_RESET_PASSWORD).toBoolean()

    if (forceResetPassword) {
      context.challenge(context.form().createForm(RESET_CREDENTIALS_FORM_TPL))
      return
    }
    context.success()
  }

  override fun action(context: AuthenticationFlowContext) {
    val formData = context.httpRequest.decodedFormParameters
    val oldPassword = formData.getFirst("oldPassword")
    val newPassword = formData.getFirst("newPassword")
    val confirmPassword = formData.getFirst("confirmPassword")
    val user = context.user

    if (oldPassword.isNullOrEmpty() || newPassword.isNullOrEmpty() || confirmPassword.isNullOrEmpty() || user == null) {
      context.challenge(
        context.form()
          .addError(FormMessage("volMissingInput"))
          .createForm(RESET_CREDENTIALS_FORM_TPL)
      )
      return
    }
    if (oldPassword == newPassword || oldPassword == confirmPassword) {
      context.challenge(
        context.form()
          .addError(FormMessage("volPasswordDuplicate"))
          .createForm(RESET_CREDENTIALS_FORM_TPL)
      )
      return
    }
    if (newPassword != confirmPassword) {
      context.challenge(
        context.form()
          .addError(FormMessage("volPasswordMismatch"))
          .createForm(RESET_CREDENTIALS_FORM_TPL)
      )
      return
    }

    if (!repository.updatePassword(
        UpdatePasswordRequest(
          userId = StorageId(user.id).externalId,
          newPassword = newPassword,
          currentPassword = oldPassword
        )
      )
    ) {
      context.challenge(
        context.form()
          .addError(FormMessage("volPasswordUpdateError"))
          .createForm(RESET_CREDENTIALS_FORM_TPL)
      )
      return
    }
    context.success()
  }

  override fun requiresUser(): Boolean {
    return false
  }

  override fun configuredFor(
    session: KeycloakSession?,
    realm: RealmModel?,
    user: UserModel?
  ): Boolean {
    return false
  }

  override fun setRequiredActions(
    session: KeycloakSession?,
    realm: RealmModel?,
    user: UserModel?
  ) {
  }

  override fun close() {
  }

}
