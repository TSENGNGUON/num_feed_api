package org.example.instragramclone.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.instragramclone.auth.dto.request.AuthenticationRequest;
import org.example.instragramclone.auth.dto.request.RegisterRequest;
import org.example.instragramclone.auth.dto.response.AuthenticationResponse;
import org.example.instragramclone.auth.dto.response.UserInfo;
import org.example.instragramclone.user.repository.UserRepository;
import org.example.instragramclone.auth.service.AuthenticationService;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.security.jwt.JwtService;
import org.example.instragramclone.common.Role;
import org.example.instragramclone.user.dto.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public ApiResponse<AuthenticationResponse> register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        UserInfo userInfo = UserInfo.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .build();
        var authResponse = AuthenticationResponse
                .builder()
                .token(jwtToken)
                .user(userInfo)
                .build();
        return ApiResponse.success(authResponse, "User registered successfully");
    }

    @Override
    public ApiResponse<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        UserInfo userInfo = UserInfo.builder()
                .name(user.getUsername())
                .email(user.getEmail())
                .build();
        var authResponse = AuthenticationResponse
                .builder()
                .token(jwtToken)
                .user(userInfo)
                .build();
        return ApiResponse.success(authResponse, "Authentication successful");
    }
}

