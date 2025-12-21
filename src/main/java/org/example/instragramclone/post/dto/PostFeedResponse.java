package org.example.instragramclone.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostFeedResponse {
    private UUID id;
    private String caption;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AuthorDto author;
    private List<String> imageUrls;
    private Long likesCount;
    private Long repostsCount;
    private Long savesCount;
    private Boolean isReposted;
}