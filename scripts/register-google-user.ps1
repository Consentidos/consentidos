param(
    [Parameter(Mandatory = $true)]
    [string]$IdToken,
    [string]$EnvFile = '.env'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. (Join-Path $PSScriptRoot 'common.ps1')

function ConvertFrom-Base64Url {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Value
    )

    $normalized = $Value.Replace('-', '+').Replace('_', '/')
    switch ($normalized.Length % 4) {
        2 { $normalized += '==' }
        3 { $normalized += '=' }
    }

    return [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($normalized))
}

$parts = $IdToken.Split('.')
if ($parts.Length -lt 2) {
    throw 'El id_token no tiene formato JWT valido.'
}

$payloadJson = ConvertFrom-Base64Url -Value $parts[1]
$payload = $payloadJson | ConvertFrom-Json

if ([string]::IsNullOrWhiteSpace($payload.sub)) {
    throw 'El payload del id_token no contiene el campo sub.'
}

$values = @{
    'AUTH_USER_0_GOOGLE_SUB' = [string]$payload.sub
    'AUTH_USER_0_EMAIL' = [string]$payload.email
    'AUTH_USER_0_DISPLAY_NAME' = [string]$payload.name
    'AUTH_USER_0_ACTIVE' = 'true'
}

Set-DotEnvValues -EnvFile $EnvFile -Values $values

Write-Host 'Se actualizaron estos valores en .env:'
Write-Host "- AUTH_USER_0_GOOGLE_SUB=$($values['AUTH_USER_0_GOOGLE_SUB'])"
Write-Host "- AUTH_USER_0_EMAIL=$($values['AUTH_USER_0_EMAIL'])"
Write-Host "- AUTH_USER_0_DISPLAY_NAME=$($values['AUTH_USER_0_DISPLAY_NAME'])"
Write-Host "- AUTH_USER_0_ACTIVE=true"

