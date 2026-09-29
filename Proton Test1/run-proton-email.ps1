$ErrorActionPreference = 'Stop'

$username = Read-Host 'Proton username'
if ([string]::IsNullOrWhiteSpace($username)) {
    throw 'A Proton username is required.'
}

$securePassword = Read-Host 'Proton password' -AsSecureString
if ($securePassword.Length -eq 0) {
    throw 'A Proton password is required.'
}

$passwordPointer = [IntPtr]::Zero
try {
    $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $env:FRAMEWORK_PROTON_USERNAME = $username
    $env:FRAMEWORK_PROTON_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)

    $maven = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if ($null -ne $maven) {
        $mavenPath = $maven.Source
    } else {
        $mavenPath = Join-Path $env:LOCALAPPDATA 'Programs\Apache\Maven\apache-maven-3.9.16\bin\mvn.cmd'
    }

    if (-not (Test-Path -LiteralPath $mavenPath)) {
        throw 'Maven was not found. Install Maven 3.9+ or add mvn.cmd to PATH.'
    }

    Push-Location $PSScriptRoot
    try {
        & $mavenPath --batch-mode --no-transfer-progress test -Dheadless=false
        $mavenExitCode = $LASTEXITCODE
    } finally {
        Pop-Location
    }
} finally {
    if ($passwordPointer -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    }
    Remove-Item Env:FRAMEWORK_PROTON_USERNAME -ErrorAction SilentlyContinue
    Remove-Item Env:FRAMEWORK_PROTON_PASSWORD -ErrorAction SilentlyContinue
    $securePassword.Dispose()
}

exit $mavenExitCode
