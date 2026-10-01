package com.portocale.volunteer.kc.provider

import com.portocale.volunteer.kc.repository.BackendRepository
import java.util.concurrent.ConcurrentHashMap
import org.keycloak.component.ComponentModel
import org.keycloak.models.KeycloakSession
import org.keycloak.provider.ProviderConfigProperty
import org.keycloak.provider.ProviderConfigurationBuilder
import org.keycloak.storage.UserStorageProviderFactory

/** Registers the Volunteer_BE user federation provider and builds it from the admin console config. */
class VolunteerUserStorageProviderFactory : UserStorageProviderFactory<VolunteerUserStorageProvider> {
  private val clients =
    ConcurrentHashMap<String, BackendRepository>()

  override fun getId(): String = PROVIDER_ID

  override fun getHelpText(): String = "Authenticates users against the Volunteer backend API"

  override fun getConfigProperties(): List<ProviderConfigProperty> =
    ProviderConfigurationBuilder.create()
      .property()
      .name(CONFIG_BASE_URL)
      .label("Backend URL")
      .helpText("Base URL of Volunteer_BE, for example https://volunteer-be-rs60.onrender.com")
      .type(ProviderConfigProperty.STRING_TYPE)
      .defaultValue(DEFAULT_BASE_URL)
      .add()
      .property()
      .name(CONFIG_USERNAME)
      .label("Service username")
      .helpText("Basic auth user the provider uses to call the backend")
      .type(ProviderConfigProperty.STRING_TYPE)
      .defaultValue(DEFAULT_USERNAME)
      .add()
      .property()
      .name(CONFIG_PASSWORD)
      .label("Service password")
      .helpText("Password for the service user, stored encrypted by Keycloak")
      .type(ProviderConfigProperty.PASSWORD)
      .secret(true)
      .add()
      .build()

  override fun create(session: KeycloakSession, model: ComponentModel): VolunteerUserStorageProvider {

    val repository =
      clients.computeIfAbsent(model.id) {
        BackendRepository(
          baseUrl = model.get(CONFIG_BASE_URL),
          username = model.get(CONFIG_USERNAME),
          password = model.get(CONFIG_PASSWORD)
        )
      }

    return VolunteerUserStorageProvider(session, model, repository)
  }

  companion object {
    const val PROVIDER_ID = "volunteer-be"

    private const val CONFIG_BASE_URL = "backendUrl"
    private const val CONFIG_USERNAME = "serviceUsername"
    private const val CONFIG_PASSWORD = "servicePassword"

    private const val DEFAULT_BASE_URL = "https://volunteer-be-rs60.onrender.com"
    private const val DEFAULT_USERNAME = "keycloak"
  }
}
