package com.owlsecurity.portal.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.owlsecurity.portal.service.CloudinaryService;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin("*")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    // Upload Multiple Images (Max 3)

    @PostMapping("/images")
    public List<String> uploadImages(
            @RequestParam("files") MultipartFile[] files
    ) throws IOException {

        if (files == null || files.length == 0) {

            throw new RuntimeException(
                    "Please select at least one image"
            );
        }

        if (files.length > 3) {

            throw new RuntimeException(
                    "Maximum 3 images allowed"
            );
        }

        List<String> imageUrls =
                new ArrayList<>();

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue;
            }

            String contentType =
                    file.getContentType();

            if (contentType == null ||
                    !contentType.startsWith("image/")) {

                throw new RuntimeException(
                        "Only image files are allowed"
                );
            }

            if (file.getSize() >
                    5 * 1024 * 1024) {

                throw new RuntimeException(
                        "Each image must be under 5MB"
                );
            }

            String imageUrl =
                    cloudinaryService.uploadFile(file);

            imageUrls.add(imageUrl);
        }

        return imageUrls;
    }

    // Upload Single Video

    @PostMapping("/video")
    public String uploadVideo(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        if (file == null || file.isEmpty()) {

            throw new RuntimeException(
                    "Please select a video"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("video/")) {

            throw new RuntimeException(
                    "Only video files are allowed"
            );
        }

        if (file.getSize() >
                50 * 1024 * 1024) {

            throw new RuntimeException(
                    "Video size must be under 50MB"
            );
        }

        return cloudinaryService.uploadFile(file);
    }
}