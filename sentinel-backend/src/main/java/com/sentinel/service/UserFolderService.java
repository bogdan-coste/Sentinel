package com.sentinel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserFolderService {

    @Value("${app.upload.base-dir}")
    private String baseUploadDir;

    private static final Logger log = LoggerFactory.getLogger(UserFolderService.class);

    public void createUserFolders(String userFolderName) throws IOException {

        if (userFolderName == null || userFolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("User folder name cannot be null or empty");
        }

        Path userDir = Paths.get(baseUploadDir, "users", userFolderName);
        Path profilePicDir = userDir.resolve("profile");
        Path profileBannerDir = userDir.resolve("banner");
        Path profilePostsDir = userDir.resolve("posts");

        Files.createDirectories(profilePicDir);
        Files.createDirectories(profileBannerDir);
        Files.createDirectories(profilePostsDir);

        log.info("Created user folders at: {}", userDir.toAbsolutePath());
    }

    public void createFolder(String folderName){
        Path userDir = Paths.get(baseUploadDir, "users", folderName);
        Path profilePicDir = userDir.resolve(folderName);
    }

    public boolean checkIfExists(Path pathToFolder){
        return Files.exists(pathToFolder) && Files.isDirectory(pathToFolder);
    }
}
