package org.example.instragramclone.auth.service;

import org.example.instragramclone.auth.dto.request.AuthenticationRequest;
import org.example.instragramclone.auth.dto.request.RegisterRequest;
import org.example.instragramclone.auth.dto.response.AuthenticationResponse;
import org.example.instragramclone.common.dto.response.ApiResponse;

public interface AuthenticationService {
    ApiResponse<AuthenticationResponse> register(RegisterRequest request);
    ApiResponse<AuthenticationResponse> authenticate(AuthenticationRequest request);
}
