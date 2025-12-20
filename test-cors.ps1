# Script para probar CORS en NakMuay API
# Ejecutar en PowerShell

Write-Host "================================" -ForegroundColor Cyan
Write-Host "  PRUEBAS CORS - NakMuay API" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

$apiUrl = "https://nakmuay-api-nfg4.onrender.com/api"
$localUrl = "http://localhost:8080/api"

Write-Host "Selecciona el entorno a probar:" -ForegroundColor Yellow
Write-Host "1. Producción (Render)" -ForegroundColor White
Write-Host "2. Local (localhost:8080)" -ForegroundColor White
$env = Read-Host "Opción"

if ($env -eq "2") {
    $baseUrl = $localUrl
    Write-Host "Probando entorno LOCAL..." -ForegroundColor Green
} else {
    $baseUrl = $apiUrl
    Write-Host "Probando entorno PRODUCCIÓN..." -ForegroundColor Green
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Prueba 1: OPTIONS Preflight (localhost:8100)" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Cyan

$headers1 = @{
    "Origin" = "http://localhost:8100"
    "Access-Control-Request-Method" = "POST"
    "Access-Control-Request-Headers" = "content-type"
}

try {
    $response1 = Invoke-WebRequest -Uri "$baseUrl/planes" -Method OPTIONS -Headers $headers1 -UseBasicParsing
    Write-Host "✅ Status: $($response1.StatusCode)" -ForegroundColor Green
    Write-Host "Headers CORS:" -ForegroundColor Cyan
    $response1.Headers.GetEnumerator() | Where-Object { $_.Key -like "*Access-Control*" } | ForEach-Object {
        Write-Host "  $($_.Key): $($_.Value)" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Prueba 2: OPTIONS Preflight (Capacitor)" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Cyan

$headers2 = @{
    "Origin" = "capacitor://localhost"
    "Access-Control-Request-Method" = "GET"
    "Access-Control-Request-Headers" = "authorization,content-type"
}

try {
    $response2 = Invoke-WebRequest -Uri "$baseUrl/horarios" -Method OPTIONS -Headers $headers2 -UseBasicParsing
    Write-Host "✅ Status: $($response2.StatusCode)" -ForegroundColor Green
    Write-Host "Headers CORS:" -ForegroundColor Cyan
    $response2.Headers.GetEnumerator() | Where-Object { $_.Key -like "*Access-Control*" } | ForEach-Object {
        Write-Host "  $($_.Key): $($_.Value)" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Prueba 3: GET Planes (localhost:8100)" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Cyan

$headers3 = @{
    "Origin" = "http://localhost:8100"
}

try {
    $response3 = Invoke-WebRequest -Uri "$baseUrl/planes" -Method GET -Headers $headers3 -UseBasicParsing
    Write-Host "✅ Status: $($response3.StatusCode)" -ForegroundColor Green
    Write-Host "Headers CORS:" -ForegroundColor Cyan
    $response3.Headers.GetEnumerator() | Where-Object { $_.Key -like "*Access-Control*" } | ForEach-Object {
        Write-Host "  $($_.Key): $($_.Value)" -ForegroundColor White
    }
    
    $json = $response3.Content | ConvertFrom-Json
    Write-Host "Datos recibidos: $($json.Count) planes" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Prueba 4: GET Usuarios (Angular:4200)" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Cyan

$headers4 = @{
    "Origin" = "http://localhost:4200"
}

try {
    $response4 = Invoke-WebRequest -Uri "$baseUrl/usuarios" -Method GET -Headers $headers4 -UseBasicParsing
    Write-Host "✅ Status: $($response4.StatusCode)" -ForegroundColor Green
    Write-Host "Headers CORS:" -ForegroundColor Cyan
    $response4.Headers.GetEnumerator() | Where-Object { $_.Key -like "*Access-Control*" } | ForEach-Object {
        Write-Host "  $($_.Key): $($_.Value)" -ForegroundColor White
    }
    
    $json = $response4.Content | ConvertFrom-Json
    Write-Host "Datos recibidos: $($json.Count) usuarios" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "  PRUEBAS COMPLETADAS" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Resultados esperados:" -ForegroundColor Yellow
Write-Host "✅ Status: 200 OK" -ForegroundColor White
Write-Host "✅ Access-Control-Allow-Origin: [origen solicitado]" -ForegroundColor White
Write-Host "✅ Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS" -ForegroundColor White
Write-Host "✅ Access-Control-Allow-Headers: Authorization, Content-Type, Accept, X-Requested-With, Origin" -ForegroundColor White
Write-Host "✅ Access-Control-Allow-Credentials: true" -ForegroundColor White
Write-Host ""
