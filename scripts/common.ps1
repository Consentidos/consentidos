Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Get-ProjectRoot {
    return (Split-Path -Parent $PSScriptRoot)
}

function Get-EnvFilePath {
    param(
        [string]$EnvFile
    )

    if ([string]::IsNullOrWhiteSpace($EnvFile)) {
        return (Join-Path (Get-ProjectRoot) '.env')
    }

    if ([System.IO.Path]::IsPathRooted($EnvFile)) {
        return $EnvFile
    }

    return (Join-Path (Get-ProjectRoot) $EnvFile)
}

function Get-DotEnvMap {
    param(
        [string]$EnvFile
    )

    $resolvedEnvFile = Get-EnvFilePath -EnvFile $EnvFile
    if (-not (Test-Path $resolvedEnvFile)) {
        throw "No se encontro el archivo .env en '$resolvedEnvFile'."
    }

    $map = @{}
    foreach ($line in [System.IO.File]::ReadAllLines($resolvedEnvFile)) {
        if ([string]::IsNullOrWhiteSpace($line) -or $line.TrimStart().StartsWith('#')) {
            continue
        }

        $parts = $line.Split('=', 2)
        if ($parts.Length -ne 2) {
            continue
        }

        $map[$parts[0]] = $parts[1]
    }

    return $map
}

function Set-DotEnvValues {
    param(
        [string]$EnvFile,
        [hashtable]$Values
    )

    $resolvedEnvFile = Get-EnvFilePath -EnvFile $EnvFile
    if (-not (Test-Path $resolvedEnvFile)) {
        throw "No se encontro el archivo .env en '$resolvedEnvFile'."
    }

    $lines = [System.Collections.Generic.List[string]]::new()
    $lines.AddRange([System.IO.File]::ReadAllLines($resolvedEnvFile))

    foreach ($key in $Values.Keys) {
        $updated = $false
        for ($i = 0; $i -lt $lines.Count; $i++) {
            if ($lines[$i] -match ('^' + [regex]::Escape($key) + '=')) {
                $lines[$i] = "$key=$($Values[$key])"
                $updated = $true
                break
            }
        }

        if (-not $updated) {
            $lines.Add("$key=$($Values[$key])")
        }
    }

    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllLines($resolvedEnvFile, $lines, $utf8NoBom)
}

function Get-FirstGoogleClientId {
    param(
        [string]$EnvFile
    )

    $map = Get-DotEnvMap -EnvFile $EnvFile
    $rawValue = $map['AUTH_GOOGLE_CLIENT_IDS']
    if ([string]::IsNullOrWhiteSpace($rawValue)) {
        throw 'AUTH_GOOGLE_CLIENT_IDS esta vacio en el archivo .env.'
    }

    return ($rawValue -split ',')[0].Trim()
}

