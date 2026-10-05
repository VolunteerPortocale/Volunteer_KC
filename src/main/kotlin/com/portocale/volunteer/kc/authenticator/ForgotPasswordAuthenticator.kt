package com.portocale.volunteer.kc.authenticator

import com.portocale.volunteer.kc.ResetPasswordRequest
import com.portocale.volunteer.kc.repository.BackendRepository
import com.portocale.volunteer.kc.repository.CommonModels.Companion.FormAttributes
import org.keycloak.authentication.AuthenticationFlowContext
import org.keycloak.authentication.Authenticator
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.UserModel
import org.keycloak.models.utils.FormMessage

private const val FORGOT_PASSWORD_FORM_TPL = "forgot_password.ftl"
private const val FORGOT_PASSWORD_CONFIRM_FORM_TPL = "forgot_password_confirm.ftl"

class ForgotPasswordAuthenticator(
  private val repository: BackendRepository
) : Authenticator {

  override fun authenticate(context: AuthenticationFlowContext) {
    val formData = context.httpRequest.decodedFormParameters
    val username = formData.getFirst(FormAttributes.USERNAME.value)
    context.challenge(
      context.form()
        .setAttribute(FormAttributes.USERNAME.value, username)
        .createForm(FORGOT_PASSWORD_FORM_TPL)
    )
  }

  override fun action(context: AuthenticationFlowContext) {
    val formData = context.httpRequest.decodedFormParameters
    val username = formData.getFirst(FormAttributes.USERNAME.value)

    if (username.isNullOrEmpty()) {
      context.challenge(
        context.form()
          .addError(FormMessage("volMissingInput"))
          .setAttribute(FormAttributes.USERNAME.value, username)
          .createForm(FORGOT_PASSWORD_FORM_TPL)
      )
      return
    }

    if (repository.resetPassword(ResetPasswordRequest(email = username))) {
      context.challenge(
        context.form()
          .setAttribute(FormAttributes.USERNAME.value, username)
          .createForm(FORGOT_PASSWORD_CONFIRM_FORM_TPL)
      )
      return
    } else {
      context.challenge(
        context.form()
          .addError(FormMessage("volForgotPasswordError"))
          .setAttribute(FormAttributes.USERNAME.value, username)
          .createForm(FORGOT_PASSWORD_FORM_TPL)
      )
      return
    }
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
