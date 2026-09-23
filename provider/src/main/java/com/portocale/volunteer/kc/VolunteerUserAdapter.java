package com.portocale.volunteer.kc;

import org.keycloak.models.UserModel;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Presents a Volunteer_BE user as a Keycloak UserModel. Read-only: users are
 * managed in the app, not here.
 */
public class VolunteerUserAdapter extends AbstractUserAdapter {

    private final VolunteerUser user;

    public VolunteerUserAdapter(KeycloakSession session, RealmModel realm,
            ComponentModel model, VolunteerUser user) {
        super(session, realm, model);
        this.user = user;
        this.storageId = new StorageId(model.getId(), user.id);
    }

    @Override
    public String getUsername() {
        return user.email;
    }

    @Override
    public String getEmail() {
        return user.email;
    }

    @Override
    public String getFirstName() {
        return user.firstName;
    }

    @Override
    public String getLastName() {
        return user.lastName;
    }

    @Override
    public boolean isEmailVerified() {
        return !"INACTIVE".equals(user.status);
    }

    /**
     * Disabled users cannot log in, which is how INACTIVE and SUSPENDED are
     * enforced.
     */
    @Override
    public boolean isEnabled() {
        return user.isLoginAllowed();
    }

    /**
     * Maps the single BE role onto a realm role with the same name, if that role
     * exists.
     */
    @Override
    protected Set<RoleModel> getRoleMappingsInternal() {
        if (user.role == null) {
            return Collections.emptySet();
        }
        RoleModel role = realm.getRole(user.role);
        return role == null ? Collections.emptySet() : Collections.singleton(role);
    }

    /**
     * No credentials are stored in Keycloak for these users: the backend validates
     * passwords.
     */
    @Override
    public SubjectCredentialManager credentialManager() {
        return session.users().getUserCredentialManager(this);
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        if (UserModel.USERNAME.equals(name) || UserModel.EMAIL.equals(name)) {
            return user.email == null ? Stream.empty() : Stream.of(user.email);
        }
        if (UserModel.FIRST_NAME.equals(name)) {
            return user.firstName == null ? Stream.empty() : Stream.of(user.firstName);
        }
        if (UserModel.LAST_NAME.equals(name)) {
            return user.lastName == null ? Stream.empty() : Stream.of(user.lastName);
        }
        return Stream.empty();
    }
}
