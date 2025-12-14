package org.example.instragramclone.mdoel.FileUpdaod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetaData {
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
}
