# Testing Facebook OAuth Without UI

This guide shows you how to get a real Facebook access token and test it with your backend API.

---

## Method 1: Using Facebook Graph API Explorer (Easiest)

### Step 1: Get Access Token

1. **Go to Facebook Graph API Explorer**: https://developers.facebook.com/tools/explorer/

2. **Select or Create an App**:
   - Click the dropdown next to "Meta App" at the top
   - Select your app or create a new one at https://developers.facebook.com/apps/

3. **Generate User Access Token**:
   - Click "Generate Access Token" button
   - Select permissions: `email`, `public_profile`
   - Click "Generate Access Token"
   - **Copy the token** - it will look like: `EAABwzLixnjYBO7ZC...`

4. **Verify Token Works**:
   - In the Graph API Explorer, set the endpoint to: `me?fields=id,name,email`
   - Click "Submit" - you should see your Facebook userDto info

### Step 2: Test with Your API

#### Using Swagger UI:
1. Open: `http://localhost:8080/swagger-ui.html`
2. Find: `POST /api/v1/auth/oauth/facebook`
3. Click "Try it out"
4. Enter:
   ```json
   {
     "accessToken": "YOUR_FACEBOOK_ACCESS_TOKEN_HERE",
     "email": "your-facebook-email@example.com",
     "name": "Your Name"
   }
   ```
5. Click "Execute"

#### Using cURL:
```bash
curl -X POST "http://localhost:8080/api/v1/auth/oauth/facebook" \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "YOUR_FACEBOOK_ACCESS_TOKEN_HERE",
    "email": "your-facebook-email@example.com",
    "name": "Your Name"
  }'
```

#### Using PowerShell:
```powershell
$body = @{
    accessToken = "YOUR_FACEBOOK_ACCESS_TOKEN_HERE"
    email = "your-facebook-email@example.com"
    name = "Your Name"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/oauth/facebook" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

---

## Method 2: Using Facebook Login SDK (For Mobile/Web Apps)

If you're building a mobile or web app, you can use Facebook's SDKs:

### For Web (JavaScript):
```javascript
FB.login(function(response) {
    if (response.authResponse) {
        const accessToken = response.authResponse.accessToken;
        // Send this token to your backend
        fetch('http://localhost:8080/api/v1/auth/oauth/facebook', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                accessToken: accessToken,
                email: response.authResponse.email,
                name: response.authResponse.name
            })
        });
    }
}, {scope: 'email,public_profile'});
```

### For Android:
Use Facebook Android SDK to get the access token, then send it to your backend.

---

## Method 3: Using Postman

1. **Get Access Token** (use Method 1 above)
2. **Create New Request**:
   - Method: `POST`
   - URL: `http://localhost:8080/api/v1/auth/oauth/facebook`
   - Headers: `Content-Type: application/json`
   - Body (raw JSON):
     ```json
     {
       "accessToken": "YOUR_TOKEN_HERE",
       "email": "test@example.com",
       "name": "Test User"
     }
     ```
3. **Send** the request

---

## How It Works

1. **Token Verification**: The backend calls Facebook's Graph API (`https://graph.facebook.com/me`) to verify the token
2. **User Info Extraction**: Extracts `id`, `name`, and `email` from Facebook's response
3. **User Creation/Update**: 
   - If email exists: Updates provider to FACEBOOK
   - If new: Creates userDto with FACEBOOK provider
4. **JWT Generation**: Returns a JWT token for your app

---

## Important Notes

### Token Expiration
- **User Access Tokens** from Graph API Explorer expire after 1-2 hours
- **Long-lived tokens** can last up to 60 days
- For production, implement token refresh logic

### Token Permissions
- Make sure your Facebook app has `email` permission enabled
- Some users might not have email associated with Facebook account

### Fallback Behavior
- If token verification fails, the backend will use the email/name from your request
- This allows testing even if token is expired (for development only)

### Production Considerations
- Always verify tokens in production
- Handle token expiration gracefully
- Implement proper error handling
- Consider using Facebook's official SDKs for better security

---

## Testing Scenarios

### Scenario 1: Valid Token
- Use a fresh token from Graph API Explorer
- **Expected**: User created/updated, JWT token returned

### Scenario 2: Expired Token
- Use an old/expired token
- **Expected**: Backend falls back to request data, still works (for development)

### Scenario 3: Invalid Token
- Use a random string as token
- **Expected**: Backend falls back to request data

### Scenario 4: Missing Email Permission
- Use token without email permission
- **Expected**: Email might be null, backend uses request email

---

## Quick Test Script

Save as `test-facebook.ps1`:

```powershell
# Replace with your actual Facebook access token
$accessToken = "YOUR_FACEBOOK_ACCESS_TOKEN"
$email = "your-email@example.com"
$name = "Your Name"

$body = @{
    accessToken = $accessToken
    email = $email
    name = $name
} | ConvertTo-Json

Write-Host "Testing Facebook OAuth Login..." -ForegroundColor Green

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/oauth/facebook" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host "Success! Token received:" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 3)
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host $_.Exception.Response
}
```

Run with: `.\test-facebook.ps1`

---

## Troubleshooting

### Error: "Invalid OAuth access token"
- Token might be expired - generate a new one
- Check if token has required permissions

### Error: "Email not found"
- User might not have email on Facebook
- Backend will use email from request as fallback

### Error: "Network error"
- Check internet connection
- Verify Facebook Graph API is accessible
- Check firewall settings

---

## Next Steps

1. **Get a real token** using Method 1
2. **Test the endpoint** using Swagger, cURL, or Postman
3. **Check the response** - you should get a JWT token
4. **Use the JWT token** to access protected endpoints

For production, consider:
- Implementing proper token refresh
- Adding rate limiting
- Better error handling
- Logging and monitoring

