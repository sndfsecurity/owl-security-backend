package com.owlsecurity.portal.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final String UPLOAD_DIR =
            "uploads/";

    @PostMapping
    public String uploadImage(
            @RequestParam("file")
            MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            return null;
        }

        String fileName =
                System.currentTimeMillis()
                + "_"
                + file.getOriginalFilename();

        Path path =
                Paths.get(
                        UPLOAD_DIR
                        + fileName
                );

        Files.createDirectories(
                path.getParent()
        );

        Files.write(
                path,
                file.getBytes()
        );

        return fileName;
    }
}