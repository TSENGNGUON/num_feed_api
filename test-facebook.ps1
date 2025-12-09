# PowerShell script to test Facebook OAuth endpoint
# Usage: .\test-facebook.ps1 -AccessToken "YOUR_TOKEN" -Email "your@email.com" -Name "Your Name"

param(
    [Parameter(Mandatory=$true)]
    [string]$AccessToken,
    
    [Parameter(Mandatory=$true)]
    [string]$Email,
    
    [string]$Name = ""
)

$baseUrl = "http://localhost:8080"

Write-Host "=== Testing Facebook OAuth Login ===" -ForegroundColor Green
Write-Host "Access Token: $($AccessToken.Substring(0, [Math]::Min(20, $AccessToken.Length)))..." -ForegroundColor Yellow
Write-Host "Email: $Email" -ForegroundColor Yellow
Write-Host "Name: $Name" -ForegroundColor Yellow
Write-Host ""

$body = @{
    accessToken = $AccessToken
    email = $Email
    name = if ($Name) { $Name } else { $Email.Split("@")[0] }
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/oauth/facebook" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host "✓ Success! Facebook login successful" -ForegroundColor Green
    Write-Host ""
    Write-Host "Response:" -ForegroundColor Cyan
    Write-Host ($response | ConvertTo-Json -Depth 3)
    
    if ($response.data -and $response.data.token) {
        Write-Host ""
        Write-Host "JWT Token (first 50 chars): $($response.data.token.Substring(0, [Math]::Min(50, $response.data.token.Length)))..." -ForegroundColor Green
        Write-Host ""
        Write-Host "You can now use this token to access protected endpoints:" -ForegroundColor Yellow
        Write-Host "  Authorization: Bearer $($response.data.token)" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ Error occurred!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error Details:" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host $responseBody -ForegroundColor Red
    } else {
        Write-Host $_.Exception.Message -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Green

