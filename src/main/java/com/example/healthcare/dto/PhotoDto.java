package com.example.healthcare.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PhotoDto {

    private String originalFilename;
    private String contentType;
    private byte[] file;
}
