package com.portocale.volunteer.kc.authenticator

import com.portocale.volunteer.kc.VolunteerUserAdapter.Companion.FORCE_RESET_PASSWORD
import com.portocale.volunteer.kc.repository.BackendRepository
import org.keycloak.authentication.AuthenticationFlowContext
import org.keycloak.authentication.Authenticator
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.UserModel
import org.keycloak.models.utils.FormMessage

private const val RESET_CREDENTIALS_FORM_TPL = "reset_credentials.ftl"

class ResetCredentialsAuthenticator(
  private val repository: BackendRepository
) : Authenticator {

  override fun authenticate(context: AuthenticationFlowContext) {
    val user = context.user
    if (user.getFirstAttribute(FORCE_RESET_PASSWORD).toBoolean()) {
      context.challenge(context.form().createForm(RESET_CREDENTIALS_FORM_TPL))
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
    }
    if (oldPassword == newPassword || oldPassword == confirmPassword) {
      context.challenge(
        context.form()
          .addError(FormMessage("volPasswordDuplicate"))
          .createForm(RESET_CREDENTIALS_FORM_TPL)
      )
    }
    if (newPassword != confirmPassword) {
      context.challenge(
        context.form()
          .addError(FormMessage("volPasswordMismatch"))
          .createForm(RESET_CREDENTIALS_FORM_TPL)
      )
    }
//    Here we need to call the reset password from the BE and in case of 200 return success,
//    otherwise error based on the response 400 for the invalid creds and generic error in case of any other
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
