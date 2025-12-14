package org.example.instragramclone.user.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.instragramclone.auth.dto.response.UserInfo;
import org.example.instragramclone.user.service.UserService;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<UserInfo>> getProfile(Authentication authentication) {
        ApiResponse<UserInfo> response = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/current-user/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserInfo>> uploadProfileImage(Authentication authentication,
                                                                     @RequestPart("file") MultipartFile file) throws Exception {
        ApiResponse<UserInfo> response = userService.updateProfileImage(authentication, file);
        return ResponseEntity.ok(response);
    }
}

