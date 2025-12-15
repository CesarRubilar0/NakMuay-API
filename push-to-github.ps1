<#
Script seguro para inicializar y subir el proyecto a GitHub desde PowerShell.
No incluye tokens en texto claro. Te pedirá confirmación antes de sobrescribir remotes.
#>

Param(
    [string]$RepoUrl = "https://github.com/CesarRubilar0/avance-spring.git",
    [string]$Branch = "main"
)

Write-Host "Directorio actual: $(Get-Location)"

# Comprobar si git está instalado
try {
    git --version > $null 2>&1
} catch {
    Write-Error "git no está instalado o no está en PATH. Instala Git for Windows e inténtalo de nuevo."
    exit 1
}

# Inicializar repo si no existe
if (-not (Test-Path ".git")) {
    Write-Host "Inicializando repositorio git..."
    git init
} else {
    Write-Host "Repositorio git ya inicializado."
}

# Añadir y commitear
Write-Host "Añadiendo archivos al índice y realizando commit (si hay cambios)..."
git add .
# Solo commitear si hay cambios
auto $status = git status --porcelain
if ($status) {
    git commit -m "Backup: subida inicial del proyecto Spring"
} else {
    Write-Host "No hay cambios para commitear."
}

# Forzar rama principal
git branch -M $Branch

# Gestionar remote origin
$existing = git remote | Select-String -Pattern "^origin$" -SimpleMatch
if (-not $existing) {
    Write-Host "Añadiendo remote origin => $RepoUrl"
    git remote add origin $RepoUrl
} else {
    $current = git remote get-url origin
    if ($current -ne $RepoUrl) {
        Write-Host "Remote origin existente: $current"
        $resp = Read-Host "¿Deseas reemplazarlo por $RepoUrl? (y/N)"
        if ($resp -eq 'y' -or $resp -eq 'Y') {
            git remote set-url origin $RepoUrl
        } else {
            Write-Host "Se mantiene el remote actual. Continuando..."
        }
    } else {
        Write-Host "Remote origin ya apunta al URL deseado."
    }
}

# Push
Write-Host "Haciendo push a origin/$Branch ..."
try {
    git push -u origin $Branch
} catch {
    Write-Error "Error al hacer push. Revisa autenticación y remote."
    exit 1
}

Write-Host "Push completado. Si hubo problemas de autenticación, usa un PAT o configura SSH."