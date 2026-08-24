param(
    [int]$Port = 5500,
    [string]$EnvFile = '.env',
    [switch]$GenerateOnly,
    [switch]$OpenBrowser
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. (Join-Path $PSScriptRoot 'common.ps1')

$projectRoot = Get-ProjectRoot
$resolvedEnvFile = Get-EnvFilePath -EnvFile $EnvFile
$clientId = Get-FirstGoogleClientId -EnvFile $resolvedEnvFile

$templatePath = Join-Path $PSScriptRoot 'google-identity-template.html'
if (-not (Test-Path $templatePath)) {
    throw "No se encontro la plantilla '$templatePath'."
}

$generatedDir = Join-Path $PSScriptRoot '.generated'
if (-not (Test-Path $generatedDir)) {
    New-Item -ItemType Directory -Path $generatedDir | Out-Null
}

$generatedFile = Join-Path $generatedDir 'google-login.html'
$template = Get-Content -Path $templatePath -Raw
$template = $template.Replace('__GOOGLE_CLIENT_ID__', $clientId)
$template = $template.Replace('__PORT__', [string]$Port)

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($generatedFile, $template, $utf8NoBom)

Write-Host "HTML generado en: $generatedFile"
Write-Host "Recorda agregar este origin en Google Cloud Console: http://localhost:$Port"

if ($GenerateOnly) {
    return
}

if (-not (Get-Command python -ErrorAction SilentlyContinue)) {
    throw 'No se encontro python en PATH. Instala Python o ejecuta este HTML desde otro servidor estatico.'
}

if ($OpenBrowser) {
    Start-Process "http://localhost:$Port/google-login.html"
}

Write-Host "Sirviendo archivos desde $generatedDir en http://localhost:$Port/google-login.html"
Write-Host 'Presiona Ctrl + C para detener el servidor.'
Push-Location $generatedDir
try {
    python -m http.server $Port
} finally {
    Pop-Location
}

