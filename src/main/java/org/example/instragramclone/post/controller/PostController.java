package org.example.instragramclone.post.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.common.dto.response.PageResponse;
import org.example.instragramclone.post.dto.CreatePostRequest;
import org.example.instragramclone.post.dto.PostFeedResponse;
import org.example.instragramclone.post.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostFeedResponse>> createPost(
            @RequestParam(value = "caption", required = false) String caption,
            @RequestPart("images") List<MultipartFile> images,
            Authentication authentication) {
        CreatePostRequest request = new CreatePostRequest();
        request.setCaption(caption);
        request.setImages(images);
        
        ApiResponse<PostFeedResponse> response = postService.createPost(request, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostFeedResponse>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<PostFeedResponse> response = postService.getFeed(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostFeedResponse>> getPostById(@PathVariable Long id) {
        ApiResponse<PostFeedResponse> response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @PathVariable Long id,
            Authentication authentication) {
        ApiResponse<String> response = postService.deletePost(id, authentication);
        return ResponseEntity.ok(response);
    }
}
