package com.portocale.volunteer.kc.authenticator

import org.keycloak.authentication.AuthenticationFlowContext
import org.keycloak.authentication.Authenticator
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.UserModel

private const val LOGIN_FORM_TPL = "volunteer_login.ftl"

class LoginFormAuthenticator : AbstractUsernameFormAuthenticator(), Authenticator {

  override fun authenticate(context: AuthenticationFlowContext) {
    context.challenge(context.form().createForm(LOGIN_FORM_TPL))
  }

  override fun action(context: AuthenticationFlowContext) {
    val formData = context.httpRequest.decodedFormParameters
    if (!validateUserAndPassword(context, formData)) {
      context.failureChallenge(
        ,
        context.form().createForm(LOGIN_FORM_TPL)
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
