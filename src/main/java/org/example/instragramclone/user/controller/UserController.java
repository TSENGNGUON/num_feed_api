package org.example.instragramclone.user.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.instragramclone.auth.dto.response.UserInfo;
import org.example.instragramclone.user.dto.UserFollowResponseDTO;
import org.example.instragramclone.user.service.UserService;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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

    // Follow feature endpoints
    @PostMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<String>> followUser(
            @PathVariable UUID userId,
            Authentication authentication) {
        ApiResponse<String> response = userService.followUser(userId, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<String>> unfollowUser(
            @PathVariable UUID userId,
            Authentication authentication) {
        ApiResponse<String> response = userService.unfollowUser(userId, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/follow-back")
    public ResponseEntity<ApiResponse<String>> followBack(
            @PathVariable UUID userId,
            Authentication authentication) {
        ApiResponse<String> response = userService.followBack(userId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<UserFollowResponseDTO>>> getFollowers(Authentication authentication) {
        ApiResponse<List<UserFollowResponseDTO>> response = userService.getFollowers(authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/following")
    public ResponseEntity<ApiResponse<List<UserFollowResponseDTO>>> getFollowing(Authentication authentication) {
        ApiResponse<List<UserFollowResponseDTO>> response = userService.getFollowing(authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/follow-requests")
    public ResponseEntity<ApiResponse<List<UserFollowResponseDTO>>> getFollowRequests(Authentication authentication) {
        ApiResponse<List<UserFollowResponseDTO>> response = userService.getFollowRequests(authentication);
        return ResponseEntity.ok(response);
    }
}

