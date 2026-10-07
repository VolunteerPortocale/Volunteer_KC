# Volunteer_KC

Keycloak extensions and login theme for the Volunteer Portal (volunteer.io).

This repo contains:

- **Keycloak provider (Kotlin)**: a user storage provider that reads users from Volunteer_BE, a custom login authenticator and a reset credentials authenticator. Built into one JAR.
- **Login theme** `volunteer`: page frame, CSS, JS and icons for the login and reset password pages.
- **Dockerfile** used to build the production Keycloak image on the Oracle VM.

Users are not stored in Keycloak. Keycloak asks Volunteer_BE for the user and for the password check on every login.

---

## Project structure

```
Volunteer_KC/
  build.gradle.kts, settings.gradle.kts, gradlew, gradle/   Gradle project (repo root)
  src/main/kotlin/com/portocale/volunteer/kc/
    authenticator/    login and reset credentials authenticators
    provider/         user storage provider (volunteer-be)
    repository/       HTTP client for Volunteer_BE
  src/main/resources/
    META-INF/services/                 provider registration
    theme-resources/templates/         volunteer_login.ftl, reset_credentials.ftl (shipped in the JAR)
    theme-resources/messages/          messages_en/ro/ru.properties (shipped in the JAR)
  themes/volunteer/login/              theme.properties, template.ftl, CSS, JS, icons
  Dockerfile
```

The page templates live in the JAR, the frame (`template.ftl`) and static files live in the theme folder. A change in `src/` needs a new JAR, a change in `themes/` needs the theme folder copied again.

---

## Configuration

The provider reads the Volunteer_BE connection from environment variables:

| Variable | Example | Purpose |
|---|---|---|
| `KC_BACKEND_URL` | `https://volunteer-be-rs60.onrender.com` | Volunteer_BE base URL, no trailing slash |
| `KC_BACKEND_USER` | `keycloak` | BE service user (Basic auth) |
| `KC_BACKEND_PASSWORD` | ask the team | BE service password. Never commit it |
| `KC_SPI_USER_STORAGE_PROVIDER_TIMEOUT` | `120000` | Max time in ms Keycloak waits for the provider. The default (3000) is too short when the BE wakes up on Render |

If `KC_BACKEND_URL` is missing, login fails with `KC_BACKEND_URL is not set` in the Keycloak log.

---

## Local development (Windows)

### Requirements

- JDK 21
- Keycloak 26.7.3 (zip from keycloak.org, unpacked anywhere)
- Git

In the commands below, `$kc` is the folder of your local Keycloak, for example:

```powershell
$kc = "C:\path\to\keycloak-26.7.3"
```

### 1. Build the JAR

From the repo root:

```powershell
.\gradlew clean build
dir build\libs
```

The result is `build\libs\volunteer-user-provider.jar`.

### 2. Install the JAR and theme into Keycloak

Stop Keycloak first (Ctrl+C in its window). Windows locks the JAR while Keycloak runs, so a copy made while it runs fails.

```powershell
copy build\libs\volunteer-user-provider.jar "$kc\providers\" -Force
Copy-Item -Recurse -Force themes\volunteer "$kc\themes\"
dir "$kc\providers"
```

Check that the JAR in `providers` has the current time. There must be only one provider JAR there.

### 3. Start Keycloak

```powershell
cd $kc
$env:KC_BACKEND_URL="https://volunteer-be-rs60.onrender.com"
$env:KC_BACKEND_USER="keycloak"
$env:KC_BACKEND_PASSWORD="PASTE_THE_SERVICE_PASSWORD"
$env:KC_SPI_USER_STORAGE_PROVIDER_TIMEOUT="120000"
.\bin\kc.bat start-dev --debug 8787
```

The variables only exist in that PowerShell window. Tip: keep these lines in a `start-kc.ps1` file outside the repo.

On a fresh install (empty `data` folder) also set the admin before the first start:

```powershell
$env:KC_BOOTSTRAP_ADMIN_USERNAME="admin"
$env:KC_BOOTSTRAP_ADMIN_PASSWORD="admin"
```

Wait for `Keycloak 26.7.3 ... started`.

### 4. Set up the realm (first time only)

Admin console: http://localhost:8080/admin

Always check that the realm dropdown (top left) shows **volunteer** and not **master** before changing anything.

1. **Create realm** `volunteer` (lowercase, same as production). Realm settings, General: display name `volunteer.io`.
2. **User federation**: add **Volunteer-be providers**, display name `volunteer-be`, Save.
3. **Authentication**, browser flow `volunteer-fe-flow` (same structure as production):
   ```
   volunteer-fe-flow
   ├── Cookie                                     ALTERNATIVE
   └── LoginForms (sub-flow)                      ALTERNATIVE
       ├── VolunteerFormAuthenticator             REQUIRED
       └── VolunteerResetCredentialsAuthenticator REQUIRED
   ```
   Bind it as **Browser flow**.
4. **Reset credentials flow** `forgotPasswordVol`: copy the structure from production (see "Inspect production settings" below) and bind it as **Reset credentials flow**.
5. **Realm settings, Login**: Login with email on, Forgot password on, User registration off.
6. **Realm settings, Localization**: Internationalization on, locales en, ro, ru, default en.
7. **Realm settings, Themes**: Login theme `volunteer`. Click Save once and wait for the confirmation.

The same can be done with the CLI. Log in first:

```powershell
.\bin\kcadm.bat config credentials --server http://localhost:8080 --realm master --user admin --password admin
```

Examples:

```powershell
# realm settings
.\bin\kcadm.bat update realms/volunteer -s displayName=volunteer.io -s loginTheme=volunteer -s loginWithEmailAllowed=true -s registrationAllowed=false -s resetPasswordAllowed=true -s internationalizationEnabled=true -s defaultLocale=en -s "supportedLocales[0]=en" -s "supportedLocales[1]=ro" -s "supportedLocales[2]=ru"

# browser flow with the LoginForms sub-flow
.\bin\kcadm.bat create authentication/flows -r volunteer -s alias=volunteer-fe-flow -s providerId=basic-flow -s topLevel=true -s builtIn=false
.\bin\kcadm.bat create authentication/flows/volunteer-fe-flow/executions/execution -r volunteer -s provider=auth-cookie
.\bin\kcadm.bat create authentication/flows/volunteer-fe-flow/executions/flow -r volunteer -s alias=LoginForms -s type=basic-flow -s description=LoginForms
.\bin\kcadm.bat create authentication/flows/LoginForms/executions/execution -r volunteer -s provider=credentials-login-form
.\bin\kcadm.bat create authentication/flows/LoginForms/executions/execution -r volunteer -s provider=reset-credentials-form
```

New executions start as Disabled. Set the requirements in the admin console (Authentication, volunteer-fe-flow), then bind:

```powershell
.\bin\kcadm.bat update realms/volunteer -s browserFlow=volunteer-fe-flow

# check
.\bin\kcadm.bat get realms/volunteer --fields browserFlow,resetCredentialsFlow,loginTheme,displayName
.\bin\kcadm.bat get authentication/flows/volunteer-fe-flow/executions -r volunteer --fields displayName,providerId,requirement,level,index
```

The **master** realm must keep the defaults (`browser` flow, `keycloak.v2` theme). It is only for the admin login.

### 5. Test

Open a new Incognito window: http://localhost:8080/realms/volunteer/account

- the volunteer.io login page with the EN/RO/RU dropdown
- wrong password shows an error on the same page
- a Volunteer_BE user can log in
- a user with `forceResetPassword: true` gets the "Reset your password" page

### Debugging

- **Remote debug**: in IntelliJ add a *Remote JVM Debug* configuration, host `localhost`, port `8787`, then set breakpoints in the Kotlin code.
- **Events**: Realm settings, Events, User events settings, Save events on. The `error` field of `LOGIN_ERROR` tells what failed.

| Symptom | Cause |
|---|---|
| Old page or old behaviour after a change | Keycloak still runs the old JAR. Compare the time of `build\libs` and `$kc\providers` |
| `KC_BACKEND_URL is not set` | Environment variables not set in the Keycloak window |
| `timeout` / `InterruptedException` then `user_not_found` | Volunteer_BE on Render is asleep. Open the BE URL, wait, try again |
| `unexpected status 401` | Wrong `KC_BACKEND_USER` / `KC_BACKEND_PASSWORD` |
| `invalid_user_credentials` | User found, BE rejected the password (check `forceResetPassword` on the user) |
| Settings seem ignored | They were made in the **master** realm instead of **volunteer** |
| Page looks unstyled after a deploy | Browser cache. Reload with Ctrl+Shift+R |

---

## Production (Oracle Cloud VM)

### How it runs

- Domain: https://volunteer-kc.duckdns.org
- The VM runs Docker Compose from `~/keycloak`: **postgres**, **keycloak** and **caddy** (HTTPS reverse proxy).
- The Keycloak image is built from `~/Volunteer_KC` (this repo, branch **main**) using the `Dockerfile`.
- Environment variables are set in `~/keycloak/docker-compose.yml`. Secrets go in `~/keycloak/.env` (readable only by the owner, never committed).

### Connect

```powershell
ssh -o ServerAliveInterval=60 -i "PATH\TO\ssh-key.key" ubuntu@VM_IP
```

Ask the team for the key and the IP. Keep the key **outside** the repo.

If SSH times out, the VM may be stopped: Oracle Cloud console, Compute, Instances, select the instance, Start. The containers start on their own (`restart: unless-stopped`).

### Deploy a new version

1. Merge the changes into **main** on GitHub. The VM only builds main.
2. On the VM:
   ```bash
   cd ~/Volunteer_KC
   git checkout main
   git pull
   git log --oneline -1          # must match the latest commit on GitHub

   cd ~/keycloak
   docker compose build keycloak
   docker compose up -d keycloak
   docker compose logs -f keycloak   # wait for "started", then Ctrl+C
   ```
3. If the build finishes in about a second with every step `CACHED`, nothing new was pulled. Check step 1.
4. Test (new Incognito window):
   - https://volunteer-kc.duckdns.org/realms/volunteer/account (login, wrong password, language switch)
   - https://volunteer-kc.duckdns.org/admin must still show the default Keycloak login

### Change environment variables or secrets

```bash
nano ~/keycloak/docker-compose.yml     # or ~/keycloak/.env for secrets
docker compose config > /dev/null && echo OK
docker compose up -d keycloak          # recreates the container with the new values
docker compose exec keycloak printenv | grep KC_BACKEND
```

YAML is strict: environment lines under `keycloak:` use 6 spaces, `depends_on:` uses 4. A plain `restart` does not pick up changes to the compose file, use `up -d`.

### Inspect production settings

```bash
cd ~/keycloak
docker compose exec keycloak /opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin
docker compose exec keycloak /opt/keycloak/bin/kcadm.sh get realms/volunteer --fields browserFlow,resetCredentialsFlow,loginTheme,displayName
docker compose exec keycloak /opt/keycloak/bin/kcadm.sh get authentication/flows/volunteer-fe-flow/executions -r volunteer --fields displayName,providerId,requirement,level,index
docker compose exec keycloak /opt/keycloak/bin/kcadm.sh get authentication/flows/forgotPasswordVol/executions -r volunteer --fields displayName,providerId,requirement,level,index
```

Run the `config credentials` line on its own: it asks for the admin password.

### Check what is inside the running image

```bash
docker compose exec keycloak ls /opt/keycloak/providers
docker compose exec keycloak ls /opt/keycloak/themes/volunteer/login/resources/assets
docker compose exec keycloak grep -c vol-password-toggle /opt/keycloak/themes/volunteer/login/resources/css/volunteer.css
```

### Rollback

- **Code**: on the VM, `git checkout <previous-commit>` in `~/Volunteer_KC`, then build and `up -d` as above. Return to main with `git checkout main` afterwards.
- **Login flow**: bind the built-in `browser` flow again (Authentication, browser, Action, Bind flow, Browser flow). This restores the default login immediately.

---

## Notes

- **Browser cache**: in production Keycloak serves CSS/JS with a long cache. After changing them, bump the version in the URL (`css/volunteer.css?v=N` in `theme.properties`, `js/index.js?v=N` in the `.ftl` files) so users get the new files.
- **Secrets**: never commit passwords or keys. `.gitignore` blocks `*.key` and `*.pem`. Check `git status` before every commit.
- **Render cold start**: the free tier sleeps after inactivity. The first login after a pause can be slow or time out.
