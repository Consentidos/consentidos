param(
    [string]$EnvFile = '.env'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

. (Join-Path $PSScriptRoot 'common.ps1')

function Resolve-JavaBinary {
    param(
        [string]$BinaryName
    )

    $fromPath = Get-Command $BinaryName -ErrorAction SilentlyContinue
    if ($fromPath) {
        return $fromPath.Source
    }

    if ($env:JAVA_HOME) {
        $candidate = Join-Path $env:JAVA_HOME "bin\\$BinaryName.exe"
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    throw "No se encontro '$BinaryName'. Asegurate de tener un JDK 17 instalado y JAVA_HOME configurado."
}

$javac = Resolve-JavaBinary -BinaryName 'javac'
$java = Resolve-JavaBinary -BinaryName 'java'

$sourceFile = Join-Path $PSScriptRoot 'GenerateJwtKeys.java'
if (-not (Test-Path $sourceFile)) {
    throw "No se encontro el helper Java '$sourceFile'."
}

$buildDir = Join-Path $PSScriptRoot '.generated\java'
if (-not (Test-Path $buildDir)) {
    New-Item -ItemType Directory -Path $buildDir -Force | Out-Null
}

& $javac -d $buildDir $sourceFile | Out-Null
$output = & $java -cp $buildDir GenerateJwtKeys
if (-not $output) {
    throw 'No se pudieron generar las llaves RSA.'
}

$values = @{}
foreach ($line in $output) {
    $parts = $line.Split('=', 2)
    if ($parts.Length -eq 2) {
        $values[$parts[0]] = $parts[1]
    }
}

if (-not $values.ContainsKey('AUTH_TOKEN_PRIVATE_KEY_PEM') -or -not $values.ContainsKey('AUTH_TOKEN_PUBLIC_KEY_PEM')) {
    throw 'La salida generada no contiene ambas llaves RSA esperadas.'
}

Set-DotEnvValues -EnvFile $EnvFile -Values $values

Write-Host 'Llaves RSA generadas y guardadas en .env:'
Write-Host "- AUTH_TOKEN_PRIVATE_KEY_PEM (longitud): $($values['AUTH_TOKEN_PRIVATE_KEY_PEM'].Length)"
Write-Host "- AUTH_TOKEN_PUBLIC_KEY_PEM  (longitud): $($values['AUTH_TOKEN_PUBLIC_KEY_PEM'].Length)"
Write-Host 'Listo. Ya puedes usar estas llaves para firmar y validar JWT RS256 en el backend.'

