package com.portocale.volunteer.kc;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

/**
 * Registers the Volunteer_BE user federation provider and builds it from the
 * admin console config.
 */
public class VolunteerUserStorageProviderFactory
        implements UserStorageProviderFactory<VolunteerUserStorageProvider> {

    public static final String PROVIDER_ID = "volunteer-be";

    static final String CONFIG_BASE_URL = "backendUrl";
    static final String CONFIG_USERNAME = "serviceUsername";
    static final String CONFIG_PASSWORD = "servicePassword";
    static final String CONFIG_TIMEOUT = "timeoutSeconds";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Authenticates users against the Volunteer backend API";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property()
                .name(CONFIG_BASE_URL)
                .label("Backend URL")
                .helpText("Base URL of Volunteer_BE, for example https://volunteer-be-rs60.onrender.com")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("https://volunteer-be-rs60.onrender.com")
                .add()
                .property()
                .name(CONFIG_USERNAME)
                .label("Service username")
                .helpText("Basic auth user the provider uses to call the backend")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("keycloak")
                .add()
                .property()
                .name(CONFIG_PASSWORD)
                .label("Service password")
                .helpText("Password for the service user, stored encrypted by Keycloak")
                .type(ProviderConfigProperty.PASSWORD)
                .secret(true)
                .add()
                .property()
                .name(CONFIG_TIMEOUT)
                .label("Timeout (seconds)")
                .helpText("Keep this high enough to survive a Render cold start")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("60")
                .add()
                .build();
    }

    @Override
    public VolunteerUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        String baseUrl = model.get(CONFIG_BASE_URL, "");
        String username = model.get(CONFIG_USERNAME, "keycloak");
        String password = model.get(CONFIG_PASSWORD, "");
        int timeout = parseTimeout(model.get(CONFIG_TIMEOUT, "60"));

        BackendClient backend = new BackendClient(baseUrl, username, password, timeout);
        return new VolunteerUserStorageProvider(session, model, backend);
    }

    private int parseTimeout(String raw) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return 60;
        }
    }
}
