<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <form novalidate="novalidate" id="vol-form-forgot-pasword" class="vol-login-wrapper" action="${url.loginAction}"
          method="post">
        <h4 class="vol-subtitle">${msg("volForgotPassword")}</h4>
        <h1 class="vol-title">${msg("volForgotPasswordSubtitle")}</h1>
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
            <button class="vol-button" type="submit"> ${msg("volForgotPasswordButton")} </button>
        </div>
    </form>
    <script async src="${url.resourcesPath}/js/index.js" type="text/javascript"></script>
</@layout.registrationLayout>