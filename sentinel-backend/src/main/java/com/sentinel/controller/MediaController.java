package com.sentinel.controller;

import com.sentinel.entity.UserEntity;
import com.sentinel.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
public class MediaController {

    @Value("${app.upload.base-dir}")
    private String baseUploadDir;

    private final UserService userService;

    public MediaController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/uploads/public/{publicId}/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String publicId,
                                              @PathVariable String type,
                                              @PathVariable String filename) {
        Optional<UserEntity> userOpt = userService.findByPublicId(publicId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserEntity userEntity = userOpt.get();

        Path file = Paths.get(baseUploadDir, "users", userEntity.getFolderName(), type, filename);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        String contentType = determineContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private String determineContentType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
}