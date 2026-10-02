<#macro registrationLayout displayInfo=false displayMessage=true displayRequiredFields=false showAnotherWayIfPresent=true>
    <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
            "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <meta charset="utf-8">
        <meta http-equiv="Content-type" content="text/html;charset=UTF-8"/>
        <meta name="robots" content="noindex, nofollow">

        <#--dynamic metadata-->
        <#if properties.meta?has_content>
            <#list properties.meta?split(' ') as meta>
                <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
            </#list>
        </#if>

        <#--title-->
        <title>${msg("volLoginTitle")}</title>

        <#--logo-->
        <link rel="icon" href="${url.resourcesPath}/assets/vol_logo_header.ico"/>

        <#--css-->
        <#if properties.styles?has_content>
            <#list properties.styles?split(' ') as style>
                <link rel="stylesheet" href="${url.resourcesPath}/${style}">
            </#list>
        </#if>
    </head>

    <body>
    <div class="vol-form-wrapper">
        <#--Placeholder for the nested form-->
        <div class="vol-header">
            <img class="vol-logo"
                 src="${url.resourcesPath}/assets/vol_logo_header.ico"
                 alt="VOLUNTEERIO"/>
            <span class="vol-logo-text">VOLUNTEERIO</span>

            <#if realm.internationalizationEnabled  && locale.supported?size gt 1>
                <div tabindex="-1" id="langSelect" class="vol-dropdown">
                    <div class="vol-dropdown-value">
                        <span>${locale.currentLanguageTag?upper_case}</span>
                        <span class="vol-dropdown-icon-status"></span>
                    </div>
                    <div class="vol-dropdown-options">
                        <#list locale.supported as l>
                            <#if l?size gt 1>
                                <a href="${l.url}"
                                   class="vol-dropdown-option <#if locale.currentLanguageTag == l.languageTag>vol-dropdown-option-active</#if>">
                                    ${msg("volLocale_" + l.languageTag)}
                                    (${l.languageTag?upper_case})
                                    <span class="vol-dropdown-option-check"></span>
                                </a>
                            </#if>
                        </#list>
                    </div>
                </div>
            </#if>

        </div>
        <div class="vol-content vol-column">
            <div class="vol-form">
                <#nested "form">
            </div>

            <div class="vol-text-center vol-line-height-md vol-font-grey-400">
                ${msg("volPageInfo")} <b class="vol-font-primary">${msg("volNumber")}</b>
            </div>

        </div>

        <div class="vol-footer">
            <span class="vol-rights">${msg("volFooterCopyright")}</span>
            <div class="vol-privacy">
                <a href="${msg("volLegalInformationUrl")}" target="_blank">${msg("volLegalInformation")}</a>
                <a href="${msg("volLegalMattersUrl")}" target="_blank">${msg("volLegalMatters")}</a>
            </div>
        </div>
    </div>
    </body>
    </html>
</#macro>