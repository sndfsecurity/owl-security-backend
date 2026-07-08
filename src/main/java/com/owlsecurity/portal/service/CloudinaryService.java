package com.owlsecurity.portal.service;
import java.io.IOException;
import java.util.HashMap;
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
}