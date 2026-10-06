<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage="<#if messagesPerField.existsError('username','password')??>false<#else>true</#if>" displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <form novalidate="novalidate" id="vol-form-login" class="vol-login-wrapper"
          onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
        <h4 class="vol-subtitle">${msg("volLogin")}</h4>
        <h1 class="vol-title">${msg("volLoginSubtitle")}</h1>
        <div class="vol-column">
            <div class="vol-field">
                <div class="vol-field-content">
                    <img src="${url.resourcesPath}/assets/account_circle.svg"/>

                    <div class="vol-input">
                        <input name="username" id="username" placeholder="" required autocomplete="off"
                               type="text" class="vol-input" value="${username!''}">
                        <label class="vol-label" for="username">${msg("volUsernameLabel")}</label>
                    </div>
                </div>
            </div>

            <div class="vol-field">
                <div class="vol-field-content">
                    <img src="${url.resourcesPath}/assets/lock.svg"/>

                    <div class="vol-input">
                        <input id="password" name="password" placeholder="" required autocomplete="off"
                               type="password" class="vol-input">
                        <label for="password" class="vol-label">${msg("volPasswordLabel")}</label>
                    </div>

                    <button type="button" class="vol-password-toggle"
                            aria-controls="password" aria-pressed="false"
                            aria-label="${msg("showPassword")}"
                            data-label-show="${msg("showPassword")}"
                            data-label-hide="${msg("hidePassword")}"
                            onclick="togglePasswordVisibility(this, 'password')">
                        <img class="vol-icon-show" src="${url.resourcesPath}/assets/visibility.svg" alt=""/>
                        <img class="vol-icon-hide" src="${url.resourcesPath}/assets/visibility_off.svg" alt=""/>
                    </button>
                </div>

                <#if message?has_content && message.type = 'error'>
                        <div class="vol-field-errors">
                            <span id="userNameError" class="vol-field-error">
                                <img src="${url.resourcesPath}/assets/warning_outline.svg"/>
                                ${message.summary}
                            </span>
                        </div>
                    </#if>
                </div>
        </div>

        <div class="vol-column">
            <button class="vol-button" type="submit"> ${msg("volLoginButton")} </button>
        </div>
        <div class="vol-column">
            <#if realm.resetPasswordAllowed>
                <a class="vol-link vol-mt-16" href="${url.loginResetCredentialsUrl}">${msg("volResetPassword")}</a>
            </#if>
        </div>
    </form>
    <script async src="${url.resourcesPath}/js/index.js" type="text/javascript"></script>
</@layout.registrationLayout>