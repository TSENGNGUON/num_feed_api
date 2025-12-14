package org.example.instragramclone.auth.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.instragramclone.auth.dto.request.FacebookLoginRequest;
import org.example.instragramclone.auth.dto.request.GoogleLoginRequest;
import org.example.instragramclone.auth.dto.response.AuthenticationResponse;
import org.example.instragramclone.auth.dto.response.UserInfo;
import org.example.instragramclone.user.repository.UserRepository;
import org.example.instragramclone.auth.service.OAuthService;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.security.jwt.JwtService;
import org.example.instragramclone.common.AuthProvider;
import org.example.instragramclone.common.Role;
import org.example.instragramclone.user.dto.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public ApiResponse<AuthenticationResponse> loginWithGoogle(GoogleLoginRequest request) {
        // TODO: replace with real Google ID token verification
        // Here we just trust the incoming email/idToken for demo purposes.
        String email = request.getEmail();
        String idToken = request.getIdToken();
        String providerId = extractGoogleUserId(idToken); // Extract 'sub' claim from JWT
        
        // Extract name from token or use request name
        GoogleUserInfo googleUserInfo = extractGoogleUserInfo(idToken);
        String verifiedName = googleUserInfo != null && googleUserInfo.getName() != null
                ? googleUserInfo.getName()
                : (request.getName() != null && !request.getName().trim().isEmpty()
                    ? request.getName()
                    : email.split("@")[0]);
        String verifiedEmail = googleUserInfo != null && googleUserInfo.getEmail() != null
                ? googleUserInfo.getEmail()
                : email;
        
        String name = verifiedName;
        String finalEmail = verifiedEmail;

        UserDto userDto = userRepository.findByEmail(finalEmail)
                .map(existing -> updateProvider(existing, AuthProvider.GOOGLE, providerId))
                .orElseGet(() -> {
                    String uniqueUsername = generateUniqueUsername(name, finalEmail);
                    return createProviderUser(uniqueUsername, finalEmail, providerId, AuthProvider.GOOGLE);
                });

        String token = jwtService.generateToken(userDto);
        UserInfo userInfo = UserInfo.builder()
                .name(verifiedName)
                .email(userDto.getEmail())
                .build();
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .token(token)
                .user(userInfo)
                .build();
        return ApiResponse.success(authResponse, "Google login successful");
    }

    @Override
    @Transactional
    public ApiResponse<AuthenticationResponse> loginWithFacebook(FacebookLoginRequest request) {
        String accessToken = request.getAccessToken();
        String email = request.getEmail();
        String name = request.getName();
        
        // Verify token and get user info from Facebook
        FacebookUserInfo fbUserInfo = verifyFacebookToken(accessToken);
        
        // Use verified info if available, otherwise fall back to request data
        String verifiedEmail = fbUserInfo != null && fbUserInfo.getEmail() != null 
                ? fbUserInfo.getEmail() 
                : email;
        String verifiedName = fbUserInfo != null && fbUserInfo.getName() != null 
                ? fbUserInfo.getName() 
                : (name != null && !name.trim().isEmpty() && !name.equalsIgnoreCase("string")
                    ? name 
                    : verifiedEmail.split("@")[0]);
        String providerId = fbUserInfo != null && fbUserInfo.getId() != null 
                ? fbUserInfo.getId() 
                : accessToken;

        UserDto userDto = userRepository.findByEmail(verifiedEmail)
                .map(existing -> updateProvider(existing, AuthProvider.FACEBOOK, providerId))
                .orElseGet(() -> {
                    String uniqueUsername = generateUniqueUsername(verifiedName, verifiedEmail);
                    return createProviderUser(uniqueUsername, verifiedEmail, providerId, AuthProvider.FACEBOOK);
                });

        String token = jwtService.generateToken(userDto);
        UserInfo userInfo = UserInfo.builder()
                .name(verifiedName)
                .email(userDto.getEmail())
                .build();
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .token(token)
                .user(userInfo)
                .build();
        return ApiResponse.success(authResponse, "Facebook login successful");
    }

    private UserDto updateProvider(UserDto userDto, AuthProvider provider, String providerId) {
        userDto.setProvider(provider);
        userDto.setProviderId(providerId);
        return userRepository.save(userDto);
    }

    private UserDto createProviderUser(String username, String email, String providerId, AuthProvider provider) {
        UserDto userDto = UserDto.builder()
                .username(username)
                .email(email)
                .password("N/A")
                .role(Role.USER)
                .provider(provider)
                .providerId(providerId)
                .build();
        return userRepository.save(userDto);
    }

    private String extractGoogleUserId(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length >= 2) {
                String payload = parts[1];
                byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
                String payloadJson = new String(decodedBytes);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(payloadJson);
                String sub = jsonNode.get("sub").asText();
                return sub != null ? sub : idToken.substring(0, Math.min(100, idToken.length()));
            }
        } catch (Exception e) {
            // If token parsing fails, use a hash or truncated version
            // For now, just use the token itself (column is now TEXT, so it can handle it)
        }
        return idToken;
    }

    private GoogleUserInfo extractGoogleUserInfo(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length >= 2) {
                String payload = parts[1];
                byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
                String payloadJson = new String(decodedBytes);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(payloadJson);
                
                GoogleUserInfo userInfo = new GoogleUserInfo();
                if (jsonNode.has("name")) {
                    userInfo.setName(jsonNode.get("name").asText());
                }
                if (jsonNode.has("email")) {
                    userInfo.setEmail(jsonNode.get("email").asText());
                }
                return userInfo;
            }
        } catch (Exception e) {
            // If token parsing fails, return null
        }
        return null;
    }

    private FacebookUserInfo verifyFacebookToken(String accessToken) {
        try {
            String url = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("id")) {
                FacebookUserInfo userInfo = new FacebookUserInfo();
                userInfo.setId((String) response.get("id"));
                userInfo.setName((String) response.get("name"));
                userInfo.setEmail((String) response.get("email"));
                return userInfo;
            }
        } catch (Exception e) {
            // Token verification failed - will use fallback data from request
            // In production, you might want to log this or throw an exception
        }
        return null;
    }

    private String generateUniqueUsername(String name, String email) {
        // Sanitize name: remove special characters, spaces, convert to lowercase
        String baseUsername = name != null ? name : email.split("@")[0];
        baseUsername = baseUsername.toLowerCase()
                .replaceAll("[^a-z0-9]", "")
                .trim();
        
        // Handle invalid names like "string"
        if (baseUsername.isEmpty() || baseUsername.equalsIgnoreCase("string")) {
            baseUsername = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
        }
        
        // Limit to 40 characters to leave room for suffix
        if (baseUsername.length() > 40) {
            baseUsername = baseUsername.substring(0, 40);
        }
        
        // Ensure uniqueness
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.findByUsername(username).isPresent()) {
            String suffixStr = String.valueOf(suffix);
            int maxLength = 50 - suffixStr.length() - 1; // -1 for underscore
            if (baseUsername.length() > maxLength) {
                baseUsername = baseUsername.substring(0, maxLength);
            }
            username = baseUsername + "_" + suffix;
            suffix++;
            
            // Safety check to prevent infinite loop
            if (suffix > 9999) {
                username = email.split("@")[0] + "_" + System.currentTimeMillis() % 10000;
                break;
            }
        }
        
        return username;
    }

    private static class GoogleUserInfo {
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    private static class FacebookUserInfo {
        private String id;
        private String name;
        private String email;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}

