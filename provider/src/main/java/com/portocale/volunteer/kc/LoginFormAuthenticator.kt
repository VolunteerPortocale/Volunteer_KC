package com.portocale.volunteer.kc

import org.keycloak.services.messages.Messages
import org.keycloak.authentication.AuthenticationFlowContext
import org.keycloak.authentication.AuthenticationFlowError
import org.keycloak.authentication.Authenticator
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.UserModel
import org.keycloak.models.utils.KeycloakModelUtils

class LoginFormAuthenticator : AbstractUsernameFormAuthenticator(), Authenticator {
    override fun authenticate(context: AuthenticationFlowContext) {
        context.challenge(context.form().createLoginUsernamePassword())
    }

    override fun action(context: AuthenticationFlowContext) {
        val formData = context.httpRequest.decodedFormParameters
        if (!validateUserAndPassword(context, formData)) {
            context.failureChallenge(
                AuthenticationFlowError.INVALID_CREDENTIALS,
                context.form()
                    .setError(Messages.INVALID_USER)
                    .createLoginUsernamePassword()
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
