# How to Get Facebook Access Token from Meta Dashboard

## Step-by-Step Guide

### Step 1: Access Graph API Explorer

1. **From your Meta Dashboard** (the image you showed):
   - Click on **"Tools"** in the top navigation bar
   - Select **"Graph API Explorer"** from the dropdown

   OR

   - Go directly to: https://developers.facebook.com/tools/explorer/

### Step 2: Select Your App

1. In Graph API Explorer, you'll see a dropdown at the top that says **"Meta App"** or shows an app name
2. Click the dropdown and select **"Num_Feed"** (your app from the dashboard)

### Step 3: Generate Access Token

1. **Click the "Generate Access Token" button** (usually a blue button)
2. A popup will appear asking for permissions
3. **Select these permissions**:
   - ✅ `email` (to get userDto's email)
   - ✅ `public_profile` (to get userDto's name and profile info)
4. Click **"Generate Access Token"**
5. **Copy the token** - it will look like: `EAABwzLixnjYBO7ZC...` (long string)

### Step 4: Verify Token Works (Optional but Recommended)

1. In Graph API Explorer, you'll see a text field with endpoint: `me`
2. Change it to: `me?fields=id,name,email`
3. Click **"Submit"** button
4. You should see your Facebook userDto information in JSON format:
   ```json
   {
     "id": "123456789",
     "name": "Your Name",
     "email": "your.email@example.com"
   }
   ```
5. If this works, your token is valid! ✅

### Step 5: Test with Your Backend

Now use this token to test your backend API:

#### Option A: Using Swagger UI

1. Open: `http://localhost:8080/swagger-ui.html`
2. Find: `POST /api/v1/auth/oauth/facebook`
3. Click **"Try it out"**
4. Enter your data:
   ```json
   {
     "accessToken": "PASTE_YOUR_TOKEN_HERE",
     "email": "your-facebook-email@example.com",
     "name": "Your Name"
   }
   ```
5. Click **"Execute"**

#### Option B: Using PowerShell Script

```powershell
.\test-facebook.ps1 -AccessToken "YOUR_TOKEN_FROM_STEP_3" -Email "your-email@example.com" -Name "Your Name"
```

#### Option C: Using cURL

```bash
curl -X POST "http://localhost:8080/api/v1/auth/oauth/facebook" \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "YOUR_TOKEN_FROM_STEP_3",
    "email": "your-email@example.com",
    "name": "Your Name"
  }'
```

---

## Quick Reference

### Graph API Explorer URL
https://developers.facebook.com/tools/explorer/

### Your App Name
**Num_Feed**

### Required Permissions
- `email`
- `public_profile`

### Test Endpoint
`me?fields=id,name,email`

### Your Backend Endpoint
`POST http://localhost:8080/api/v1/auth/oauth/facebook`

---

## Troubleshooting

### Problem: "Token expired" or "Invalid token"
- **Solution**: Generate a new token from Graph API Explorer
- Tokens from Graph API Explorer expire after 1-2 hours

### Problem: "Missing email permission"
- **Solution**: Make sure you selected `email` permission when generating token
- Some users might not have email on Facebook (rare)

### Problem: "Cannot find Graph API Explorer"
- **Solution**: 
  1. Go to: https://developers.facebook.com/tools/explorer/
  2. Or click "Tools" → "Graph API Explorer" from dashboard

### Problem: Token works in Graph API Explorer but not in backend
- **Solution**: 
  - Check if your backend is running
  - Verify the token is copied correctly (no extra spaces)
  - Check backend logs for errors

---

## What Happens When You Test

1. ✅ Your backend receives the access token
2. ✅ Backend calls Facebook: `https://graph.facebook.com/me?fields=id,name,email&access_token=YOUR_TOKEN`
3. ✅ Facebook verifies token and returns your userDto info
4. ✅ Backend extracts: userDto ID, name, email
5. ✅ Backend creates/updates userDto in database with FACEBOOK provider
6. ✅ Backend returns JWT token for your app

---

## Next Steps After Testing

1. **Test with different tokens** to ensure it works consistently
2. **Check your database** to see the created/updated userDto
3. **Use the returned JWT token** to access protected endpoints
4. **For production**: Implement long-lived tokens and token refresh

---

## Visual Guide

```
Meta Dashboard
    ↓
Tools → Graph API Explorer
    ↓
Select "Num_Feed" app
    ↓
Generate Access Token
    ↓
Select permissions (email, public_profile)
    ↓
Copy token
    ↓
Test in Swagger/Postman/cURL
    ↓
Get JWT token from backend
    ↓
Use JWT for protected endpoints
```

