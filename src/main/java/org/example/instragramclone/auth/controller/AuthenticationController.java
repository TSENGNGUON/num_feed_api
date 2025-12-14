package org.example.instragramclone.auth.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import org.example.instragramclone.auth.dto.request.AuthenticationRequest;
import org.example.instragramclone.auth.dto.response.AuthenticationResponse;
import org.example.instragramclone.auth.service.AuthenticationService;
import org.example.instragramclone.auth.dto.request.RegisterRequest;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @RequestBody RegisterRequest request
    ) {
        ApiResponse<AuthenticationResponse> response = service.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        ApiResponse<AuthenticationResponse> response = service.authenticate(request);
        return ResponseEntity.ok(response);
    }
}

