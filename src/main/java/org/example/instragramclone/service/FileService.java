package org.example.instragramclone.service;

import org.example.instragramclone.mdoel.FileUpdaod.FileMetaData;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    FileMetaData uploadFile(MultipartFile file) throws IOException;
    Resource getFileByName(String fileName) throws IOException;

}
