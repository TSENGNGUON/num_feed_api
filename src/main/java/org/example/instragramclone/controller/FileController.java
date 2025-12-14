package org.example.instragramclone.controller;

import io.minio.errors.MinioException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.service.FileService;
import org.example.instragramclone.service.MinioService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/v1/files")
public class FileController {
//    private final FileService fileService;
      private final MinioService minioService;
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ApiResponse<?>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
//        ApiResponse<Object> response = ApiResponse.builder()
//                .message("File Upload Successful")
//                .data(fileService.uploadFile(file))
//                .success(true)
//                .build();
//        return  ResponseEntity.ok(response);
//    }


    @GetMapping("/download-file/{file-name}")
    public ResponseEntity<?> downloadFileByFileName(@PathVariable("file-name") String fileName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        Resource resource = minioService.getFileByName(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }


    // File Upload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        return ResponseEntity.ok( minioService.uploadFile(file));
    }

    // Preview Image
    @GetMapping("/preview-file/{file-name}")
    public ResponseEntity<?> previewFileByFileName(@PathVariable("file-name") String fileName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        Resource resource = minioService.getFileByName(fileName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
