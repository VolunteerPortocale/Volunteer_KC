package com.portocale.volunteer.kc.authenticator

import org.keycloak.Config
import org.keycloak.authentication.Authenticator
import org.keycloak.authentication.AuthenticatorFactory
import org.keycloak.models.AuthenticationExecutionModel.Requirement
import org.keycloak.models.KeycloakSession
import org.keycloak.models.KeycloakSessionFactory
import org.keycloak.models.credential.PasswordCredentialModel
import org.keycloak.provider.ProviderConfigProperty

private const val PROVIDER_ID = "credentials-login-form"

class LoginFormAuthenticatorFactory : AuthenticatorFactory {

  override fun create(session: KeycloakSession?): Authenticator {
    return LoginFormAuthenticator()
  }

  override fun init(config: Config.Scope?) {
  }

  override fun postInit(factory: KeycloakSessionFactory) {
  }

  override fun close() {
  }

  override fun getId(): String {
    return PROVIDER_ID
  }

  override fun getDisplayType(): String {
    return "VolunteerFormAuthenticator"
  }

  override fun getReferenceCategory(): String {
    return PasswordCredentialModel.TYPE
  }

  override fun isConfigurable(): Boolean {
    return false
  }

  override fun getRequirementChoices(): Array<out Requirement> {
    return arrayOf(Requirement.ALTERNATIVE, Requirement.DISABLED, Requirement.REQUIRED)
  }

  override fun isUserSetupAllowed(): Boolean {
    return false
  }

  override fun getHelpText(): String {
    return "Auth form for the Volunteer FE"
  }

  override fun getConfigProperties(): List<ProviderConfigProperty> {
    return emptyList()
  }

}
