package com.portocale.volunteer.kc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Talks to Volunteer_BE over HTTP using basic auth.
 * Uses the JDK HttpClient so the jar bundles no extra dependencies:
 * Keycloak provider jars share the server classloader.
 */
public class BackendClient {

    private static final Logger log = Logger.getLogger(BackendClient.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String baseUrl;
    private final String authHeader;
    private final HttpClient http;
    private final Duration timeout;

    public BackendClient(String baseUrl, String username, String password, int timeoutSeconds) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.authHeader = "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        this.http = HttpClient.newBuilder()
                .connectTimeout(this.timeout)
                .build();
    }

    /** Returns null when the BE answers 404. */
    public VolunteerUser findByEmail(String email) {
        String encoded = URLEncoder.encode(email, StandardCharsets.UTF_8);
        return get("/api/v1/users?email=" + encoded);
    }

    /** Returns null when the BE answers 404. */
    public VolunteerUser findById(String id) {
        return get("/api/v1/users/" + URLEncoder.encode(id, StandardCharsets.UTF_8));
    }

    /**
     * 200 means the password is correct, 400 means it is wrong, 404 means no such
     * user.
     * Any other status is treated as a failure so a broken BE never lets someone
     * in.
     */
    public boolean verifyPassword(String email, String password) {
        try {
            String body = MAPPER.createObjectNode()
                    .put("email", email)
                    .put("password", password)
                    .toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/users/login"))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .timeout(timeout)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 200) {
                return true;
            }
            if (status == 400 || status == 404) {
                return false;
            }
            log.warnf("Unexpected status %d from backend login", status);
            return false;
        } catch (Exception e) {
            log.error("Backend login call failed", e);
            return false;
        }
    }

    private VolunteerUser get(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Authorization", authHeader)
                    .header("Accept", "application/json")
                    .timeout(timeout)
                    .GET()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 200) {
                return MAPPER.readValue(response.body(), VolunteerUser.class);
            }
            if (status != 404) {
                log.warnf("Unexpected status %d from backend for %s", status, path);
            }
            return null;
        } catch (Exception e) {
            log.error("Backend call failed for " + path, e);
            return null;
        }
    }
}
