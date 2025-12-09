# PowerShell script to test OAuth endpoints
# Run with: .\test-oauth.ps1

$baseUrl = "http://localhost:8080"

Write-Host "=== Testing Google OAuth Login ===" -ForegroundColor Green
$googleBody = @{
    idToken = "mock-google-token-12345"
    email = "testuser@gmail.com"
    name = "Test User"
} | ConvertTo-Json

try {
    $googleResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/oauth/google" `
        -Method POST `
        -ContentType "application/json" `
        -Body $googleBody
    
    Write-Host "Success! Token received:" -ForegroundColor Green
    Write-Host ($googleResponse | ConvertTo-Json -Depth 3)
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host $_.Exception.Response
}

Write-Host "`n=== Testing Facebook OAuth Login ===" -ForegroundColor Green
$facebookBody = @{
    accessToken = "mock-facebook-token-67890"
    email = "testuser@facebook.com"
    name = "Test User"
} | ConvertTo-Json

try {
    $facebookResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/oauth/facebook" `
        -Method POST `
        -ContentType "application/json" `
        -Body $facebookBody
    
    Write-Host "Success! Token received:" -ForegroundColor Green
    Write-Host ($facebookResponse | ConvertTo-Json -Depth 3)
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host $_.Exception.Response
}

Write-Host "`n=== Testing with existing email (should update provider) ===" -ForegroundColor Yellow
$existingUserBody = @{
    idToken = "mock-google-token-update"
    email = "testuser@gmail.com"
    name = "Updated Test User"
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/oauth/google" `
        -Method POST `
        -ContentType "application/json" `
        -Body $existingUserBody
    
    Write-Host "Success! User updated:" -ForegroundColor Green
    Write-Host ($updateResponse | ConvertTo-Json -Depth 3)
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

