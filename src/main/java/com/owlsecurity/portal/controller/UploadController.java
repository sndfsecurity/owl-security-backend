package com.owlsecurity.portal.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.owlsecurity.portal.service.CloudinaryService;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping
    public String uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            return null;
        }

        return cloudinaryService.uploadFile(file);
    }
}