<#
PowerShell helper to install Maven on Windows using Chocolatey.
Run PowerShell as Administrator and execute:
    .\run-setup.ps1

The script will:
- Check for existing 'mvn' on PATH
- If missing, check for Chocolatey
- If Chocolatey missing, offer to install it (automatic)
- Install Maven via Chocolatey
- Verify installation

Note: This script requires Administrator privileges to install Chocolatey and packages.
#>

function Write-Info($m) { Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Write-Err($m) { Write-Host "[ERROR] $m" -ForegroundColor Red }

Write-Info "Checking for Maven (mvn)..."
$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvn) {
    Write-Info "Maven is already installed: $($mvn.Source)"
    & mvn -v
    exit 0
}

# Check for Chocolatey
Write-Info "Maven not found. Checking for Chocolatey..."
$choco = Get-Command choco -ErrorAction SilentlyContinue
if (-not $choco) {
    Write-Info "Chocolatey not found. Installing Chocolatey..."
    if (-not ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
        Write-Err "This script must be run as Administrator to install Chocolatey and Maven. Please re-run PowerShell as Administrator and retry."
        exit 1
    }

    Set-ExecutionPolicy Bypass -Scope Process -Force
    $chocoScript = 'https://community.chocolatey.org/install.ps1'
    try {
        Write-Info "Downloading and running Chocolatey install script..."
        iex ((New-Object System.Net.WebClient).DownloadString($chocoScript))
    } catch {
        Write-Err "Failed to install Chocolatey: $($_.Exception.Message)"
        exit 1
    }
    # reload profile to pick up choco on PATH for the current session
    refreshenv | Out-Null 2>$null
    $choco = Get-Command choco -ErrorAction SilentlyContinue
    if (-not $choco) {
        Write-Err "Chocolatey installation seemed to fail or PATH not updated. Please restart the shell and re-run this script."
        exit 1
    }
}
else {
    Write-Info "Chocolatey found: $($choco.Source)"
}

Write-Info "Installing Maven via Chocolatey..."
try {
    choco install maven -y --no-progress
} catch {
    Write-Err "Chocolatey failed to install Maven: $($_.Exception.Message)"
    exit 1
}

Write-Info "Verifying Maven installation..."
$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvn) {
    Write-Info "Maven installed successfully: $($mvn.Source)"
    & mvn -v
    Write-Info "You can now run tests in the project with:`n    cd \"$PWD\"`n    mvn clean test"
    exit 0
} else {
    Write-Err "Maven installation completed but 'mvn' not found on PATH. Please restart your shell or log out/in and try again."
    exit 1
}
