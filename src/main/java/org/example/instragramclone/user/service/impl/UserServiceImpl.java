package org.example.instragramclone.user.service.impl;

import lombok.AllArgsConstructor;
import org.example.instragramclone.auth.dto.response.UserInfo;
import org.example.instragramclone.user.repository.UserRepository;
import org.example.instragramclone.user.service.UserService;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.mdoel.FileUpdaod.FileMetaData;
import org.example.instragramclone.service.MinioService;
import org.example.instragramclone.user.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MinioService minioService;

    @Override
    public ApiResponse<UserInfo> getCurrentUser(Authentication authentication) {
        String identifier = authentication.getName();
        UserDto userDto = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserInfo userInfo = mapToUserInfo(userDto);
        return ApiResponse.success(userInfo, "Current user profile retrieved successfully");
    }

    @Override
    public ApiResponse<UserInfo> updateProfileImage(Authentication authentication, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return ApiResponse.error("INVALID_FILE", "Please provide a non-empty image file.");
        }

        String identifier = authentication.getName();
        UserDto userDto = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FileMetaData metaData = minioService.uploadFile(file);
        userDto.setImageUrl(metaData.getFileUrl());
        userRepository.save(userDto);

        return ApiResponse.success(mapToUserInfo(userDto), "Profile image updated successfully");
    }

    private UserInfo mapToUserInfo(UserDto userDto) {
        return UserInfo.builder()
                .name(userDto.getUsername())
                .email(userDto.getEmail())
                .imageUrl(userDto.getImageUrl())
                .role(userDto.getRole().toString())
                .createdAt(userDto.getCreatedAt())
                .updatedAt(userDto.getUpdatedAt())
                .build();
    }


}
