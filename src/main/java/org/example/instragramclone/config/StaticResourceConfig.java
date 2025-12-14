package org.example.instragramclone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files saved to src/main/resources/files (during dev)
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:src/main/resources/files/");

        // Optional alias if you want /images/** to also serve from the same folder
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:src/main/resources/files/");
    }
}


