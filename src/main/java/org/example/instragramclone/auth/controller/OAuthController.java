package org.example.instragramclone.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.instragramclone.auth.dto.request.FacebookLoginRequest;
import org.example.instragramclone.auth.dto.request.GoogleLoginRequest;
import org.example.instragramclone.auth.dto.response.AuthenticationResponse;
import org.example.instragramclone.auth.service.OAuthService;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request
    ) {
        ApiResponse<AuthenticationResponse> response = oAuthService.loginWithGoogle(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/facebook")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> facebookLogin(
            @Valid @RequestBody FacebookLoginRequest request
    ) {
        ApiResponse<AuthenticationResponse> response = oAuthService.loginWithFacebook(request);
        return ResponseEntity.ok(response);
    }
}

