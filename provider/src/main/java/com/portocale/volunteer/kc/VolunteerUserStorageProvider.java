package com.portocale.volunteer.kc;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

/**
 * Looks users up in Volunteer_BE and asks it to check passwords. Keycloak
 * stores no credentials for them.
 */
public class VolunteerUserStorageProvider
        implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private final KeycloakSession session;
    private final ComponentModel model;
    private final BackendClient backend;

    public VolunteerUserStorageProvider(KeycloakSession session, ComponentModel model, BackendClient backend) {
        this.session = session;
        this.model = model;
        this.backend = backend;
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        String externalId = StorageId.externalId(id);
        VolunteerUser user = backend.findById(externalId);
        return user == null ? null : new VolunteerUserAdapter(session, realm, model, user);
    }

    /** Usernames are email addresses in this system. */
    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        return getUserByEmail(realm, username);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        VolunteerUser user = backend.findByEmail(email);
        return user == null ? null : new VolunteerUserAdapter(session, realm, model, user);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        String password = input.getChallengeResponse();
        if (password == null || password.isEmpty()) {
            return false;
        }
        return backend.verifyPassword(user.getEmail(), password);
    }

    @Override
    public void close() {
        // nothing to release
    }
}
