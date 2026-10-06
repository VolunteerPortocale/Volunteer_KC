<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <form novalidate="novalidate" id="vol-form-reset-pasword" class="vol-login-wrapper" action="${url.loginAction}"
          method="post">
        <h4 class="vol-subtitle">${msg("volResetPassword")}</h4>
        <h1 class="vol-title">${msg("volResetPasswordSubtitle")}</h1>
        <div class="vol-column">
            <div class="vol-field">
                <div class="vol-field-content">
                    <div class="vol-input">
                        <input name="oldPassword" id="oldPassword" placeholder="" required autocomplete="off"
                               type="password" class="vol-input">
                        <label class="vol-label" for="oldPassword">${msg("volOldPasswordLabel")}</label>
                    </div>

                    <button type="button" class="vol-password-toggle"
                            aria-controls="oldPassword" aria-pressed="false"
                            aria-label="${msg("showPassword")}"
                            data-label-show="${msg("showPassword")}"
                            data-label-hide="${msg("hidePassword")}"
                            onclick="togglePasswordVisibility(this, 'oldPassword')">
                        <img class="vol-icon-show" src="${url.resourcesPath}/assets/visibility.svg" alt=""/>
                        <img class="vol-icon-hide" src="${url.resourcesPath}/assets/visibility_off.svg" alt=""/>
                    </button>
                </div>
            </div>

            <div class="vol-field">
                <div class="vol-field-content">
                    <div class="vol-input">
                        <input id="newPassword" name="newPassword" placeholder="" required autocomplete="off"
                               type="password" class="vol-input">
                        <label for="newPassword" class="vol-label">${msg("volNewPasswordLabel")}</label>
                    </div>

                    <button type="button" class="vol-password-toggle"
                            aria-controls="newPassword" aria-pressed="false"
                            aria-label="${msg("showPassword")}"
                            data-label-show="${msg("showPassword")}"
                            data-label-hide="${msg("hidePassword")}"
                            onclick="togglePasswordVisibility(this, 'newPassword')">
                        <img class="vol-icon-show" src="${url.resourcesPath}/assets/visibility.svg" alt=""/>
                        <img class="vol-icon-hide" src="${url.resourcesPath}/assets/visibility_off.svg" alt=""/>
                    </button>
                </div>
            </div>

            <div class="vol-field">
                <div class="vol-field-content">
                    <div class="vol-input">
                        <input id="confirmPassword" name="confirmPassword" placeholder="" required autocomplete="off"
                               type="password" class="vol-input">
                        <label for="confirmPassword" class="vol-label">${msg("volConfirmPasswordLabel")}</label>
                    </div>

                    <button type="button" class="vol-password-toggle"
                            aria-controls="confirmPassword" aria-pressed="false"
                            aria-label="${msg("showPassword")}"
                            data-label-show="${msg("showPassword")}"
                            data-label-hide="${msg("hidePassword")}"
                            onclick="togglePasswordVisibility(this, 'confirmPassword')">
                        <img class="vol-icon-show" src="${url.resourcesPath}/assets/visibility.svg" alt=""/>
                        <img class="vol-icon-hide" src="${url.resourcesPath}/assets/visibility_off.svg" alt=""/>
                    </button>
                </div>
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

        <div class="vol-column">
            <button class="vol-button" type="submit"> ${msg("volResetPasswordButton")} </button>
        </div>
    </form>
    <script async src="${url.resourcesPath}/js/index.js" type="text/javascript"></script>
</@layout.registrationLayout>