package org.example.instragramclone.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginRequest {
    @NotBlank(message = "idToken is required")
    private String idToken;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String name;
}

