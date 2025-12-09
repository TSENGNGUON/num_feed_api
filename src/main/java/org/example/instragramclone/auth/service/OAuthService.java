package org.example.instragramclone.auth.service;

import org.example.instragramclone.auth.dto.request.FacebookLoginRequest;
import org.example.instragramclone.auth.dto.request.GoogleLoginRequest;
import org.example.instragramclone.auth.dto.response.AuthenticationResponse;
import org.example.instragramclone.common.dto.response.ApiResponse;

public interface OAuthService {
    ApiResponse<AuthenticationResponse> loginWithGoogle(GoogleLoginRequest request);
    ApiResponse<AuthenticationResponse> loginWithFacebook(FacebookLoginRequest request);
}

