package org.example.instragramclone.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String name;
    private String email;
    private String imageUrl;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

