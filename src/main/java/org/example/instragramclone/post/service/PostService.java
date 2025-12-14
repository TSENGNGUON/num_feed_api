package org.example.instragramclone.post.service;

import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.common.dto.response.PageResponse;
import org.example.instragramclone.post.dto.CreatePostRequest;
import org.example.instragramclone.post.dto.PostFeedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface PostService {
    ApiResponse<PostFeedResponse> createPost(CreatePostRequest request, Authentication authentication);
    PageResponse<PostFeedResponse> getFeed(Pageable pageable);
    ApiResponse<PostFeedResponse> getPostById(Long id);
    ApiResponse<String> deletePost(Long id, Authentication authentication);
}
