package org.example.instragramclone.post.service;

import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.common.dto.response.PageResponse;
import org.example.instragramclone.post.dto.CreatePostRequest;
import org.example.instragramclone.post.dto.PostFeedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface PostService {
    ApiResponse<PostFeedResponse> createPost(CreatePostRequest request, Authentication authentication);
    PageResponse<PostFeedResponse> getFeed(Pageable pageable, Authentication authentication);
    ApiResponse<PostFeedResponse> getPostById(UUID id, Authentication authentication);
    ApiResponse<String> deletePost(UUID id, Authentication authentication);
    ApiResponse<String> repostPost(UUID postId, Authentication authentication);
    ApiResponse<String> unrepostPost(UUID postId, Authentication authentication);
}
