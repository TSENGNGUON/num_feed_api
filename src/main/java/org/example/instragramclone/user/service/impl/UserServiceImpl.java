package org.example.instragramclone.user.service.impl;

import lombok.AllArgsConstructor;
import org.example.instragramclone.auth.dto.response.UserInfo;
import org.example.instragramclone.common.exception.ResourceNotFoundException;
import org.example.instragramclone.user.dto.User;
import org.example.instragramclone.user.repository.UserRepository;
import org.example.instragramclone.user.service.UserService;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.mdoel.FileUpdaod.FileMetaData;
import org.example.instragramclone.service.MinioService;
import org.example.instragramclone.user.dto.UserFollowResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MinioService minioService;

    @Override
    public ApiResponse<UserInfo> getCurrentUser(Authentication authentication) {
        String identifier = authentication.getName();
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserInfo userInfo = mapToUserInfo(user);
        return ApiResponse.success(userInfo, "Current user profile retrieved successfully");
    }

    @Override
    public ApiResponse<UserInfo> updateProfileImage(Authentication authentication, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return ApiResponse.error("INVALID_FILE", "Please provide a non-empty image file.");
        }

        String identifier = authentication.getName();
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FileMetaData metaData = minioService.uploadFile(file);
        user.setImageUrl(metaData.getFileUrl());
        userRepository.save(user);

        return ApiResponse.success(mapToUserInfo(user), "Profile image updated successfully");
    }

    private UserInfo mapToUserInfo(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .role(user.getRole().toString())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private User getCurrentUserDto(Authentication authentication) {
        String identifier = authentication.getName();
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public ApiResponse<String> followUser(UUID userId, Authentication authentication) {
        User currentUser = getCurrentUserDto(authentication);
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (currentUser.getId().equals(userId)) {
            return ApiResponse.error("INVALID_OPERATION", "You cannot follow yourself");
        }

        if (currentUser.getFollowing().contains(targetUser)) {
            return ApiResponse.error("ALREADY_FOLLOWING", "You are already following this user");
        }

        currentUser.getFollowing().add(targetUser);
        userRepository.save(currentUser);

        return ApiResponse.success("Successfully followed user");
    }

    @Override
    @Transactional
    public ApiResponse<String> unfollowUser(UUID userId, Authentication authentication) {
        User currentUser = getCurrentUserDto(authentication);
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!currentUser.getFollowing().contains(targetUser)) {
            return ApiResponse.error("NOT_FOLLOWING", "You are not following this user");
        }

        currentUser.getFollowing().remove(targetUser);
        userRepository.save(currentUser);

        return ApiResponse.success("Successfully unfollowed user");
    }

    @Override
    @Transactional
    public ApiResponse<String> followBack(UUID userId, Authentication authentication) {
        User currentUser = getCurrentUserDto(authentication);
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (currentUser.getId().equals(userId)) {
            return ApiResponse.error("INVALID_OPERATION", "You cannot follow back yourself");
        }

        // Check if target user is following current user
        if (!targetUser.getFollowing().contains(currentUser)) {
            return ApiResponse.error("NOT_FOLLOWED_BY", "This user is not following you");
        }

        // Check if already following
        if (currentUser.getFollowing().contains(targetUser)) {
            return ApiResponse.error("ALREADY_FOLLOWING", "You are already following this user");
        }

        currentUser.getFollowing().add(targetUser);
        userRepository.save(currentUser);

        return ApiResponse.success("Successfully followed back");
    }

    @Override
    public ApiResponse<List<UserFollowResponseDTO>> getFollowers(Authentication authentication) {
        User currentUser = getCurrentUserDto(authentication);
        
        List<UserFollowResponseDTO> followers = currentUser.getFollower().stream()
                .map(user -> mapToUserFollowResponse(user, currentUser))
                .collect(Collectors.toList());

        return ApiResponse.success(followers, "Followers retrieved successfully");
    }

    @Override
    public ApiResponse<List<UserFollowResponseDTO>> getFollowing(Authentication authentication) {
        User currentUser = getCurrentUserDto(authentication);
        
        List<UserFollowResponseDTO> following = currentUser.getFollowing().stream()
                .map(user -> mapToUserFollowResponse(user, currentUser))
                .collect(Collectors.toList());

        return ApiResponse.success(following, "Following list retrieved successfully");
    }

    @Override
    public ApiResponse<List<UserFollowResponseDTO>> getFollowRequests(Authentication authentication) {
        User currentUser = getCurrentUserDto(authentication);
        
        // Users who follow current user but current user doesn't follow back
        List<UserFollowResponseDTO> followRequests = currentUser.getFollower().stream()
                .filter(user -> !currentUser.getFollowing().contains(user))
                .map(user -> mapToUserFollowResponse(user, currentUser))
                .collect(Collectors.toList());

        return ApiResponse.success(followRequests, "Follow requests retrieved successfully");
    }

    private UserFollowResponseDTO mapToUserFollowResponse(User user, User currentUser) {
        boolean isFollowing = currentUser.getFollowing().contains(user);
        boolean isFollowedBy = currentUser.getFollower().contains(user);
        
        String followStatus;
        if (isFollowing && isFollowedBy) {
            followStatus = "mutual"; // Both following each other
        } else if (isFollowing) {
            followStatus = "following_you"; // Current user is following them
        } else if (isFollowedBy) {
            followStatus = "start_following_you"; // They are following current user
        } else {
            followStatus = "none";
        }

        return UserFollowResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .followStatus(followStatus)
                .isFollowing(isFollowing)
                .isFollowedBy(isFollowedBy)
                .build();
    }

}
