<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <form novalidate="novalidate" id="vol-form-forgot-pasword" class="vol-login-wrapper" action="${url.loginAction}"
          method="post">
        <input type="hidden" id="code-validity" value="${validity}"/>
        <h4 class="vol-subtitle">${msg("volTwoFaTile")}</h4>
        <h1 class="vol-title">${msg("volTwoFaSubtitle")}</h1>
        <span id="timer" class="vol-mb-16"> ${msg("volLoginTimerText")}
            <span id="timer-countdown"></span>
        </span>
        <span hidden id="code-expired">
            <img src="${url.resourcesPath}/assets/warning_outline.svg" alt="Warning Icon"/>
                ${msg("volCodeExpired")}
        </span>
        <div class="vol-column">
            <div class="vol-field">
                <div class="vol-field-content">
                    <img src="${url.resourcesPath}/assets/lock.svg"/>
                    <div class="vol-input">
                        <input name="otp" id="otp" placeholder="" required autocomplete="off"
                               type="text" class="vol-input">
                        <label class="vol-label" for="otp">${msg("volOtpLabel")}</label>
                    </div>
                </div>
            </div>

            <#if message?has_content && message.type = 'error'>
                <div class="vol-field-errors vol-mb-16">
                        <span id="userNameError" class="vol-field-error">
                            <img src="${url.resourcesPath}/assets/warning_outline.svg"/>
                            ${message.summary}
                        </span>
                </div>
            </#if>
        </div>

        <div class="vol-column">
            <button id="two-factory-submit" class="vol-button" type="submit"> ${msg("volOtpConfirmButton")} </button>

            <span class="vol-mb-16">${msg("volCodeNotReceived")}</span>

            <a id="resendCodeBtn" class="vol-link" type="button"
               href="${url.loginUrl}&resend=true">${msg("volCodeResend")}
            </a>
        </div>
        </div>
    </form>
    <script async src="${url.resourcesPath}/js/index.js" type="text/javascript"></script>
    <script async src="${url.resourcesPath}/js/two-factory.js" type="text/javascript"></script>
</@layout.registrationLayout>