package com.portocale.volunteer.kc.provider

import com.portocale.volunteer.kc.repository.BackendRepository
import org.keycloak.component.ComponentModel
import org.keycloak.models.KeycloakSession
import org.keycloak.provider.ProviderConfigProperty
import org.keycloak.storage.UserStorageProviderFactory

/** Registers the Volunteer_BE user federation provider and builds it from the admin console config. */
class VolunteerUserStorageProviderFactory : UserStorageProviderFactory<VolunteerUserStorageProvider> {

  override fun getId(): String = PROVIDER_ID

  override fun getHelpText(): String = "Authenticates users against the Volunteer backend API"

  override fun getConfigProperties(): List<ProviderConfigProperty> = emptyList()

  override fun create(session: KeycloakSession, model: ComponentModel): VolunteerUserStorageProvider {
    return VolunteerUserStorageProvider(session, model, BackendRepository.getInstance())
  }

  companion object {
    const val PROVIDER_ID = "volunteer-be"
  }
}
