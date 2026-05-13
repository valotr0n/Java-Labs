param(
    [string]$Port = "8086"
)

Set-Location $PSScriptRoot\server
Write-Host "Starting lab6 at http://localhost:$Port/lab6/"
Write-Host "REST API: http://localhost:$Port/lab6/api/"
Write-Host "Stop server: Ctrl+C"
mvn "-Djetty.http.port=$Port" jetty:run