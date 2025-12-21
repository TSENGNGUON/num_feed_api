package org.example.instragramclone.user.service;

import org.example.instragramclone.auth.dto.response.UserInfo;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.user.dto.UserFollowResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    ApiResponse<UserInfo> getCurrentUser(Authentication authentication);
    ApiResponse<UserInfo> updateProfileImage(Authentication authentication, MultipartFile file) throws Exception;
    
    // Follow feature methods
    ApiResponse<String> followUser(UUID userId, Authentication authentication);
    ApiResponse<String> unfollowUser(UUID userId, Authentication authentication);
    ApiResponse<String> followBack(UUID userId, Authentication authentication);
    ApiResponse<List<UserFollowResponseDTO>> getFollowers(Authentication authentication);
    ApiResponse<List<UserFollowResponseDTO>> getFollowing(Authentication authentication);
    ApiResponse<List<UserFollowResponseDTO>> getFollowRequests(Authentication authentication);
}
