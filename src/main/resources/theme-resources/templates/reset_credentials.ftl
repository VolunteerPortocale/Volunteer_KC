<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <form novalidate="novalidate" id="vol-form-reset-pasword" class="vol-login-wrapper" action="${url.loginAction}" method="post">
        <h4 class="vol-subtitle">${msg("volResetPassword")}</h4>
        <h1 class="vol-title">${msg("volResetPasswordSubtitle")}</h1>
        <div class="vol-column">
            <div class="vol-field">
                <div class="vol-field-content">
                    <div class="vol-input">
                        <input name="oldPassword" id="oldPassword" placeholder="" required autocomplete="off"
                               type="text" class="vol-input" value="${username!''}">
                        <label class="vol-label" for="oldPassword">${msg("volOldPasswordLabel")}</label>
                    </div>
                </div>
            </div>

            <div class="vol-field">
                <div class="vol-field-content">
                    <div class="vol-input">
                        <input id="newPassword" name="newPassword" placeholder="" required autocomplete="off"
                               type="password" class="vol-input">
                        <label for="newPassword" class="vol-label">${msg("volNewPasswordLabel")}</label>
                    </div>
                </div>

            </div>
            <div class="vol-field">
                <div class="vol-field-content">
                    <div class="vol-input">
                        <input id="confirmPassword" name="confirmPassword" placeholder="" required autocomplete="off"
                               type="password" class="vol-input">
                        <label for="confirmPassword" class="vol-label">${msg("volConfirmPasswordLabel")}</label>
                    </div>
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
</@layout.registrationLayout>\