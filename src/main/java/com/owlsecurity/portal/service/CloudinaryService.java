package com.owlsecurity.portal.service;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {

        Map<?, ?> uploadResult =
                cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", "auto"
                        )
                );

        return uploadResult.get("secure_url").toString();
    }
    
    
    public String uploadPdf(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("PDF file is required");
        }

        String filename = file.getOriginalFilename();

        if (filename == null
                || !filename.toLowerCase().endsWith(".pdf")
                || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("PDF size must not exceed 10 MB");
        }

        String publicId = java.util.UUID.randomUUID() + ".pdf";

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "raw",
                        "folder", "owl-reports",
                        "public_id", publicId
                )
        );

        return uploadResult.get("secure_url").toString();
    }
   
    //delete file....................
    
    public void deleteFile(String fileUrl) throws IOException {

        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        String publicId = extractPublicId(fileUrl);

        String resourceType = "image";

        if (fileUrl.contains("/video/upload/")) {
            resourceType = "video";
        } else if (fileUrl.contains("/raw/upload/")) {
            resourceType = "raw";
        }

        Map<?, ?> result =
                cloudinary.uploader().destroy(
                        publicId,
                        ObjectUtils.asMap(
                                "resource_type", resourceType
                        )
                );

    }
    
    private String extractPublicId(String fileUrl) {

        String[] parts = fileUrl.split("/upload/");

        if (parts.length < 2) {
            return "";
        }

        String path = parts[1];

        path = path.replaceFirst("^v\\d+/", "");

        int lastDot = path.lastIndexOf('.');

        if (lastDot != -1) {
            path = path.substring(0, lastDot);
        }

        return path;
    }
    
    
    public void validateImages(
            List<MultipartFile> files)
    {
        if(files == null)
        {
            return;
        }

        if(files.size() > 3)
        {
            throw new RuntimeException(
                "Maximum 3 images allowed"
            );
        }

        for(MultipartFile file : files)
        {
            if(
                file.getSize()
                > 10 * 1024 * 1024
            )
            {
                throw new RuntimeException(
                    "Image size exceeds 10 MB"
                );
            }
        }
    }
}