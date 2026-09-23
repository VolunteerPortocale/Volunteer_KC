package com.portocale.volunteer.kc;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
/** What the BE returns from /api/v1/users. Only the fields Keycloak needs. */
public class VolunteerUser {

    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public String role;
    public String status;
    public String suspendedUntil;

    /**
     * INACTIVE means registration OTP was never confirmed. SUSPENDED blocks until
     * suspendedUntil passes.
     */
    public boolean isLoginAllowed() {
        if ("ACTIVE".equals(status)) {
            return true;
        }
        if ("SUSPENDED".equals(status) && suspendedUntil != null) {
            try {
                return java.time.Instant.parse(suspendedUntil).isBefore(java.time.Instant.now());
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
