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
            <figure class="vol-logo">
                <img src="${url.resourcesPath}/assets/vol_logo_header.ico"/>
            </figure>
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