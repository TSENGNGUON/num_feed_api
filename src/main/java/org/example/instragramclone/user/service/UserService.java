package org.example.instragramclone.user.service;

import org.example.instragramclone.auth.dto.response.UserInfo;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    ApiResponse<UserInfo> getCurrentUser(Authentication authentication);
    ApiResponse<UserInfo> updateProfileImage(Authentication authentication, MultipartFile file) throws Exception;
}
