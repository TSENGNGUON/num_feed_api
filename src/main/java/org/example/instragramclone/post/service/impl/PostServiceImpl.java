package org.example.instragramclone.post.service.impl;

import lombok.AllArgsConstructor;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.common.dto.response.PageResponse;
import org.example.instragramclone.common.exception.ForbiddenException;
import org.example.instragramclone.common.exception.ResourceNotFoundException;
import org.example.instragramclone.mdoel.FileUpdaod.FileMetaData;
import org.example.instragramclone.post.dto.AuthorDto;
import org.example.instragramclone.post.dto.CreatePostRequest;
import org.example.instragramclone.post.dto.PostFeedResponse;
import org.example.instragramclone.post.entity.Post;
import org.example.instragramclone.post.entity.PostImage;
import org.example.instragramclone.post.entity.Repost;
import org.example.instragramclone.post.repository.PostRepository;
import org.example.instragramclone.post.repository.RepostRepository;
import org.example.instragramclone.post.service.PostService;
import org.example.instragramclone.service.MinioService;
import org.example.instragramclone.user.dto.User;
import org.example.instragramclone.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MinioService minioService;
    private final RepostRepository repostRepository;

    @Override
    @Transactional
    public ApiResponse<PostFeedResponse> createPost(CreatePostRequest request, Authentication authentication) {
        // Get current user
        String identifier = authentication.getName();
        User author = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Validate images
        if (request.getImages() == null || request.getImages().isEmpty()) {
            return ApiResponse.error("INVALID_REQUEST", "At least one image is required");
        }

        // Upload images to MinIO
        List<PostImage> postImages = new ArrayList<>();
        for (MultipartFile image : request.getImages()) {
            if (image.isEmpty()) continue;
            
            try {
                FileMetaData metaData = minioService.uploadFile(image);
                PostImage postImage = PostImage.builder()
                        .imageUrl(metaData.getFileUrl())
                        .build();
                postImages.add(postImage);
            } catch (Exception e) {
                return ApiResponse.error("UPLOAD_ERROR", "Failed to upload image: " + e.getMessage());
            }
        }

        if (postImages.isEmpty()) {
            return ApiResponse.error("INVALID_REQUEST", "No valid images were uploaded");
        }

        // Create post
        Post post = Post.builder()
                .author(author)
                .caption(request.getCaption())
                .images(postImages)
                .build();

        // Set bidirectional relationship
        postImages.forEach(image -> image.setPost(post));

        // Save post
        Post savedPost = postRepository.save(post);

        // Build response
        PostFeedResponse response = mapToPostFeedResponse(savedPost, author);
        return ApiResponse.success(response, "Post created successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PostFeedResponse> getFeed(Pageable pageable, Authentication authentication) {
        User currentUser = null;
        if (authentication != null) {
            String identifier = authentication.getName();
            currentUser = userRepository.findByUsername(identifier)
                    .or(() -> userRepository.findByEmail(identifier))
                    .orElse(null);
        }
        
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        final User user = currentUser;
        List<PostFeedResponse> responses = posts.getContent().stream()
                .map(post -> mapToPostFeedResponse(post, user))
                .collect(Collectors.toList());

        PageResponse.PaginationMetadata pagination = PageResponse.PaginationMetadata.builder()
                .currentPage(posts.getNumber())
                .pageSize(posts.getSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .hasNext(posts.hasNext())
                .hasPrevious(posts.hasPrevious())
                .build();

        return PageResponse.success(responses, pagination, "Feed retrieved successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PostFeedResponse> getPostById(UUID id, Authentication authentication) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        User currentUser = null;
        if (authentication != null) {
            String identifier = authentication.getName();
            currentUser = userRepository.findByUsername(identifier)
                    .or(() -> userRepository.findByEmail(identifier))
                    .orElse(null);
        }

        PostFeedResponse response = mapToPostFeedResponse(post, currentUser);
        return ApiResponse.success(response, "Post retrieved successfully");
    }

    @Override
    @Transactional
    public ApiResponse<String> deletePost(UUID id, Authentication authentication) {
        String identifier = authentication.getName();
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = postRepository.findByIdAndAuthorId(id, user.getId())
                .orElseThrow(() -> {
                    Post existingPost = postRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
                    throw new ForbiddenException("You are not authorized to delete this post");
                });

        postRepository.delete(post);
        return ApiResponse.success("Post deleted successfully");
    }

    @Override
    @Transactional
    public ApiResponse<String> repostPost(UUID postId, Authentication authentication) {
        String identifier = authentication.getName();
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Check if already reposted
        if (repostRepository.existsByUserAndPost(user, post)) {
            return ApiResponse.error("ALREADY_REPOSTED", "You have already reposted this post");
        }

        Repost repost = Repost.builder()
                .user(user)
                .post(post)
                .build();

        repostRepository.save(repost);
        return ApiResponse.success("Post reposted successfully");
    }

    @Override
    @Transactional
    public ApiResponse<String> unrepostPost(UUID postId, Authentication authentication) {
        String identifier = authentication.getName();
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Repost repost = repostRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new ResourceNotFoundException("Repost not found"));

        repostRepository.delete(repost);
        return ApiResponse.success("Post unreposted successfully");
    }

    private PostFeedResponse mapToPostFeedResponse(Post post, User currentUser) {
        AuthorDto authorDto = AuthorDto.builder()
                .id(post.getAuthor().getId())
                .username(post.getAuthor().getUsername())
                .imageUrl(post.getAuthor().getImageUrl())
                .build();

        List<String> imageUrls = post.getImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());

        Long repostsCount = repostRepository.countByPostId(post.getId());
        Boolean isReposted = currentUser != null && repostRepository.existsByUserAndPost(currentUser, post);

        return PostFeedResponse.builder()
                .id(post.getId())
                .caption(post.getCaption())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(authorDto)
                .imageUrls(imageUrls)
                .likesCount(0L) // TODO: Implement likes functionality
                .repostsCount(repostsCount)
                .savesCount(0L) // TODO: Implement saves functionality
                .isReposted(isReposted)
                .build();
    }
}
