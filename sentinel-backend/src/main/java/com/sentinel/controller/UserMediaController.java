package com.sentinel.controller;

import com.sentinel.model.MediaEntity;
import com.sentinel.model.MediaResponseDto;
import com.sentinel.model.MediaType;
import com.sentinel.model.User;
import com.sentinel.service.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/users/me")
public class UserMediaController {

    private final LikeService likeService;
    @Value("${app.upload.base-dir}")
    private String baseUploadDir;

    private final UserService userService;
    private final MediaService mediaService;
    private final UserFolderService folderService;
    private final CommentService commentService;

    public UserMediaController(UserService userService, MediaService mediaService,
                               UserFolderService folderService, CommentService commentService, LikeService likeService) {
        this.userService = userService;
        this.mediaService = mediaService;
        this.folderService = folderService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePic(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "description", required = false) String description) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = (User) auth.getPrincipal();
        if (user == null || user.getFolderName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;
        Path userDir = Paths.get(baseUploadDir, "users", user.getFolderName());
        Path targetDir = userDir.resolve("profile");
        Path targetFile = targetDir.resolve(filename);
        Files.createDirectories(userDir);
        Files.createDirectories(targetDir);
        file.transferTo(targetFile.toFile());

        String oldFilename = user.getCurrentProfilePicURL();
        if (oldFilename != null) {
            mediaService.deleteByOwnerAndTypeAndFilename(user, MediaType.PROFILE_PICTURE, oldFilename);
            Path oldFile = targetDir.resolve(oldFilename);
            Files.deleteIfExists(oldFile);
        }

        MediaEntity profilePicture = new MediaEntity();
        profilePicture.setType(MediaType.PROFILE_PICTURE);
        profilePicture.setOwner(user);
        profilePicture.setPath(targetFile.toString());
        profilePicture.setContent(description);
        mediaService.save(profilePicture);

        user.setCurrentProfilePicURL(filename);
        userService.save(user);

        String publicUrl = "/uploads/public/" + user.getPublicId() + "/profile/" + filename;
        Map<String, Object> response = new HashMap<>();
        response.put("profilePicUrl", publicUrl);
        response.put("profilePicMediaId", profilePicture.getId());
        return ResponseEntity.ok(response);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/profile-banner")
    public ResponseEntity<?> uploadProfileBanner(@RequestParam("file") MultipartFile file,
                                                 @RequestParam(value = "description", required = false) String description) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = (User) auth.getPrincipal();
        if (user == null || user.getFolderName() == null || user.getFolderName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;
        Path userDir = Paths.get(baseUploadDir, "users", user.getFolderName());
        Path targetDir = userDir.resolve("banner");
        Path targetFile = targetDir.resolve(filename);
        Files.createDirectories(userDir);
        Files.createDirectories(targetDir);
        file.transferTo(targetFile.toFile());

        String oldFilename = user.getCurrentBannerPicURL();
        if (oldFilename != null) {
            mediaService.deleteByOwnerAndTypeAndFilename(user, MediaType.PROFILE_BANNER, oldFilename);
            Path oldFile = targetDir.resolve(oldFilename);
            Files.deleteIfExists(oldFile);
        }

        MediaEntity profileBanner = new MediaEntity();
        profileBanner.setContent(description);
        profileBanner.setType(MediaType.PROFILE_BANNER);
        profileBanner.setOwner(user);
        profileBanner.setPath(targetFile.toString());
        mediaService.save(profileBanner);

        user.setCurrentBannerPicURL(filename);
        userService.save(user);

        String publicUrl = "/uploads/public/" + user.getPublicId() + "/banner/" + filename;
        Map<String, Object> response = new HashMap<>();
        response.put("bannerPicUrl", publicUrl);
        response.put("bannerMediaId", profileBanner.getId());
        return ResponseEntity.ok(response);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        User user = (User) auth.getPrincipal();

        if ((content == null || content.isBlank()) && (file == null || file.isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Post must have text or an image"));
        }

        String filename = null;
        String imageUrl = null;
        Path targetFile = null;

        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            filename = UUID.randomUUID() + extension;

            Path userDir = Paths.get(baseUploadDir, "users", user.getFolderName());
            Path targetDir = userDir.resolve("posts");
            Files.createDirectories(targetDir);
            targetFile = targetDir.resolve(filename);
            file.transferTo(targetFile.toFile());

            imageUrl = "/uploads/public/" + user.getPublicId() + "/posts/" + filename;
        }

        MediaEntity post = new MediaEntity();
        post.setContent(content);
        post.setPath(targetFile != null ? targetFile.toString() : null);
        post.setOwner(user);
        post.setType(MediaType.POST);
        mediaService.save(post);

        Map<String, Object> response = new HashMap<>();
        response.put("id", post.getId());
        response.put("content", post.getContent());
        response.put("imageUrl", imageUrl);
        response.put("createdAt", post.getCreatedAt());
        response.put("likeCount", 0);
        response.put("commentCount", 0);
        response.put("likedByCurrentUser", false);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user-media")
    public ResponseEntity<?> profilePageMedia(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = (User) auth.getPrincipal();

        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MediaEntity> allUserMedia = mediaService.findAllByOwner(user);

        return ResponseEntity.status(HttpStatus.OK).body(
                allUserMedia.stream()
                        .map(media -> {
                            MediaResponseDto dto = MediaResponseDto.fromEntity(media);
                            dto.setLikedByCurrentUser(likeService.existsByUserAndMedia(user, media));
                            int commentCount = commentService.countByMedia(media);
                            dto.setCommentCount(commentCount);
                            return dto;
                        })
                        .toList()
        );
    }
}
