package org.example.instragramclone.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {
    private UUID id;
    private String name;
    private String email;
    private String imageUrl;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

