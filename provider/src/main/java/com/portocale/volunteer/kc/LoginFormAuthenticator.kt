package com.portocale.volunteer.kc

import com.webauthn4j.data.client.challenge.Challenge
import org.keycloak.services.messages.Messages
import org.keycloak.authentication.AuthenticationFlowContext
import org.keycloak.authentication.AuthenticationFlowError
import org.keycloak.authentication.Authenticator
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator
import org.keycloak.forms.login.LoginFormsProvider
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.UserModel
import jakarta.ws.rs.core.Response

private const val LOGIN_FORM_TPL = "volunteer_login.ftl"

class LoginFormAuthenticator : AbstractUsernameFormAuthenticator(), Authenticator {

    override fun authenticate(context: AuthenticationFlowContext) {
        context.challenge(createLoginForm(context.form()))
    }

    /** Used by the base class for every login and error page. */
    override fun createLoginForm(form: LoginFormsProvider): Response =
        form.createForm(LOGIN_FORM_TPL)


    override fun action(context: AuthenticationFlowContext) {
        val formData = context.httpRequest.decodedFormParameters
        if (!validateUserAndPassword(context, formData)) {
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
