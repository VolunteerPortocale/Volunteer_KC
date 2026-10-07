<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <form novalidate="novalidate" id="vol-form-reset-pasword" class="vol-login-wrapper" action="${url.loginAction}"
          method="post">
        <h4 class="vol-subtitle">${msg("volForgotPassword")}</h4>
        <h1 class="vol-title">${msg("volForgotPasswordConfirmation")}</h1>
        <div class="vol-column">
            <button class="vol-button" type="button" onclick="window.location.href='${url.loginUrl}'">
                ${msg("volBackToLogin")}
            </button>
        </div>
    </form>
    <script async src="${url.resourcesPath}/js/index.js" type="text/javascript"></script>
</@layout.registrationLayout>