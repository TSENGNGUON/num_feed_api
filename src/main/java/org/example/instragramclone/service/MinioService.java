package org.example.instragramclone.service;

import io.minio.*;
import io.minio.errors.MinioException;
import org.example.instragramclone.mdoel.FileUpdaod.FileMetaData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;
    @Value("${minio.url}")
    private String minioUrl;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public FileMetaData uploadFile(MultipartFile file) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        String originalName = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "-" + (originalName == null ? "image" : originalName);
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return FileMetaData.builder()
                .fileName(fileName)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .fileUrl(minioUrl + "/" + bucketName + "/" + fileName)
                .build();
    }

    public Resource getFileByName(String fileName) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
        return new InputStreamResource(stream);
    }
}
