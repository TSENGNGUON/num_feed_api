package org.example.instragramclone.post.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreatePostRequest {
    private String caption;
    private List<MultipartFile> images;
}