# OAuth Testing Guide (Google & Facebook Login)

## Overview
The OAuth endpoints are accessible without authentication. Currently, token verification is not implemented (marked with TODO), so you can test with mock data.

## Endpoints
- **Google Login**: `POST /api/v1/auth/oauth/google`
- **Facebook Login**: `POST /api/v1/auth/oauth/facebook`

---

## Method 1: Using Swagger UI (Easiest)

1. **Start your application**
2. **Open Swagger UI**: Navigate to `http://localhost:8080/swagger-ui.html`
3. **Find the OAuth endpoints** under `/api/v1/auth/oauth`
4. **Test Google Login**:
   - Click on `POST /api/v1/auth/oauth/google`
   - Click "Try it out"
   - Enter test data:
     ```json
     {
       "idToken": "mock-google-token-12345",
       "email": "testuser@gmail.com",
       "name": "Test User"
     }
     ```
   - Click "Execute"
   - You should receive a JWT token in the response

5. **Test Facebook Login**:
   - Click on `POST /api/v1/auth/oauth/facebook`
   - Click "Try it out"
   - Enter test data:
     ```json
     {
       "accessToken": "mock-facebook-token-67890",
       "email": "testuser@facebook.com",
       "name": "Test User"
     }
     ```
   - Click "Execute"
   - You should receive a JWT token in the response

---

## Method 2: Using cURL

### Google Login
```bash
curl -X POST "http://localhost:8080/api/v1/auth/oauth/google" \
  -H "Content-Type: application/json" \
  -d '{
    "idToken": "mock-google-token-12345",
    "email": "testuser@gmail.com",
    "name": "Test User"
  }'
```

### Facebook Login
```bash
curl -X POST "http://localhost:8080/api/v1/auth/oauth/facebook" \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "mock-facebook-token-67890",
    "email": "testuser@facebook.com",
    "name": "Test User"
  }'
```

### Expected Response
```json
{
  "success": true,
  "message": "Google login successful" // or "Facebook login successful"
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "error": null,
  "timestamp": "2025-12-08T20:30:00.000Z"
}
```

---

## Method 3: Using Postman

1. **Create a new POST request**
2. **URL**: `http://localhost:8080/api/v1/auth/oauth/google` (or `/facebook`)
3. **Headers**: 
   - `Content-Type: application/json`
4. **Body** (raw JSON):
   ```json
   {
     "idToken": "mock-google-token-12345",
     "email": "testuser@gmail.com",
     "name": "Test User"
   }
   ```
5. **Send** the request

---

## Method 4: Getting Real Tokens (For Production Testing)

### Google OAuth Token

1. **Go to Google Cloud Console**: https://console.cloud.google.com/
2. **Create/Select a Project**
3. **Enable Google + API** or **Google Identity API**
4. **Create OAuth 2.0 Credentials**:
   - Go to "APIs & Services" > "Credentials"
   - Create OAuth 2.0 Client ID
   - Set authorized redirect URIs
5. **Get ID Token**:
   - Use Google OAuth Playground: https://developers.google.com/oauthplayground/
   - Or use a test client library
   - The ID token will be a JWT that you can decode at https://jwt.io/

### Facebook Access Token

1. **Go to Facebook Developers**: https://developers.facebook.com/
2. **Create/Select an App**
3. **Get Test Access Token**:
   - Go to "Tools" > "Graph API Explorer"
   - Select your app
   - Generate a test token
   - Or use Facebook Login SDK to get a real token

### Testing with Real Tokens

Once you have real tokens, you can:
1. Update `OAuthServiceImpl` to verify the tokens
2. Extract userDto information from verified tokens
3. Test the full OAuth flow

---

## Testing Scenarios

### Scenario 1: New User Registration
- Send OAuth request with a new email
- **Expected**: User is created with provider set to GOOGLE/FACEBOOK
- **Response**: JWT token

### Scenario 2: Existing User Login
- Send OAuth request with an existing email
- **Expected**: User's provider is updated to GOOGLE/FACEBOOK
- **Response**: JWT token

### Scenario 3: Missing Required Fields
- Send request without `email` or `idToken`/`accessToken`
- **Expected**: Validation error (400 Bad Request)

### Scenario 4: Invalid Email Format
- Send request with invalid email format
- **Expected**: Validation error (400 Bad Request)

---

## Using the JWT Token

After successful OAuth login, you'll receive a JWT token. Use it to access protected endpoints:

```bash
curl -X GET "http://localhost:8080/api/v1/demo-controller" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Notes

- Currently, token verification is **not implemented** (stub implementation)
- The backend trusts the incoming email and token for demo purposes
- For production, you must implement proper token verification:
  - Google: Verify ID token using Google's public keys
  - Facebook: Verify access token by calling Facebook's Graph API
- The `providerId` field stores the token/ID for now (should store actual provider userDto ID in production)

---

## Quick Test Script

Save this as `test-oauth.sh`:

```bash
#!/bin/bash

echo "Testing Google Login..."
curl -X POST "http://localhost:8080/api/v1/auth/oauth/google" \
  -H "Content-Type: application/json" \
  -d '{
    "idToken": "test-google-123",
    "email": "test@gmail.com",
    "name": "Test User"
  }' | jq .

echo -e "\n\nTesting Facebook Login..."
curl -X POST "http://localhost:8080/api/v1/auth/oauth/facebook" \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "test-facebook-456",
    "email": "test@facebook.com",
    "name": "Test User"
  }' | jq .
```

Run with: `chmod +x test-oauth.sh && ./test-oauth.sh`

