package org.example.instragramclone.demo;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller")
@SecurityRequirement(name = "Bearer Authentication")
public class DemoController {
    @GetMapping
    public ResponseEntity<ApiResponse<String>> sayHello(){
        ApiResponse<String> response = ApiResponse.success("Hello from security endpoint", "Successfully retrieved message");
        return ResponseEntity.ok(response);
    }
}
