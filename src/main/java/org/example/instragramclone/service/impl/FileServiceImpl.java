package org.example.instragramclone.service.impl;

import org.example.instragramclone.mdoel.FileUpdaod.FileMetaData;
import org.example.instragramclone.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Value("${spring.file-upload-path}")
    private String path;
    @Override
    public FileMetaData uploadFile(MultipartFile file) throws IOException {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)){
            Files.createDirectories(filePath);
        }

        String fileName = file.getOriginalFilename();
        fileName = UUID.randomUUID() + fileName;

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/images/" + fileName).toUriString();
        Files.copy(file.getInputStream(), filePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return FileMetaData.builder()
                .fileName(fileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .fileUrl(url)
                .build();
    }

    @Override
    public Resource getFileByName(String fileName) throws IOException {
        Path filePath = Paths.get(path);
        return new InputStreamResource(Files.newInputStream(filePath.resolve(fileName)));
    }
}
