package com.sentinel.controller;

import com.sentinel.controller.base.Controller;
import com.sentinel.entity.UserEntity;
import com.sentinel.security.ValidateCurrentUser;
import com.sentinel.entity.MediaEntity;
import com.sentinel.dto.response.MediaRes;
import com.sentinel.enums.MediaType;
import com.sentinel.service.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/users/me")
public class UserMediaController extends Controller {

    private final LikeService likeService;
    @Value("${app.upload.base-dir}")
    private String baseUploadDir;

    private final UserService userService;
    private final MediaService mediaService;
    private final UserFolderService folderService;
    private final CommentService commentService;

    public UserMediaController(UserService userService, MediaService mediaService,
                               UserFolderService folderService, CommentService commentService,
                               LikeService likeService, ValidateCurrentUser validator) {
        super(validator);
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

        UserEntity currentUserEntity = getCurrentUser();

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
        Path userDir = Paths.get(baseUploadDir, "users", currentUserEntity.getFolderName());
        Path targetDir = userDir.resolve("profile");
        Path targetFile = targetDir.resolve(filename);
        Files.createDirectories(userDir);
        Files.createDirectories(targetDir);
        file.transferTo(targetFile.toFile());

        String oldFilename = currentUserEntity.getCurrentProfilePicURL();
        if (oldFilename != null) {
            mediaService.deleteByOwnerAndTypeAndFilename(currentUserEntity, MediaType.PROFILE_PICTURE, oldFilename);
            Path oldFile = targetDir.resolve(oldFilename);
            Files.deleteIfExists(oldFile);
        }

        MediaEntity profilePicture = new MediaEntity();
        profilePicture.setType(MediaType.PROFILE_PICTURE);
        profilePicture.setOwner(currentUserEntity);
        profilePicture.setPath(targetFile.toString());
        profilePicture.setContent(description);
        mediaService.save(profilePicture);

        currentUserEntity.setCurrentProfilePicURL(filename);
        userService.save(currentUserEntity);

        String publicUrl = "/uploads/public/" + currentUserEntity.getPublicId() + "/profile/" + filename;
        Map<String, Object> response = new HashMap<>();
        response.put("profilePicUrl", publicUrl);
        response.put("profilePicMediaId", profilePicture.getId());
        return ResponseEntity.ok(response);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/profile-banner")
    public ResponseEntity<?> uploadProfileBanner(@RequestParam("file") MultipartFile file,
                                                 @RequestParam(value = "description", required = false) String description) throws IOException {

        UserEntity currentUserEntity = getCurrentUser(); // ✅

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
        Path userDir = Paths.get(baseUploadDir, "users", currentUserEntity.getFolderName());
        Path targetDir = userDir.resolve("banner");
        Path targetFile = targetDir.resolve(filename);
        Files.createDirectories(userDir);
        Files.createDirectories(targetDir);
        file.transferTo(targetFile.toFile());

        String oldFilename = currentUserEntity.getCurrentBannerPicURL();
        if (oldFilename != null) {
            mediaService.deleteByOwnerAndTypeAndFilename(currentUserEntity, MediaType.PROFILE_BANNER, oldFilename);
            Path oldFile = targetDir.resolve(oldFilename);
            Files.deleteIfExists(oldFile);
        }

        MediaEntity profileBanner = new MediaEntity();
        profileBanner.setContent(description);
        profileBanner.setType(MediaType.PROFILE_BANNER);
        profileBanner.setOwner(currentUserEntity);
        profileBanner.setPath(targetFile.toString());
        mediaService.save(profileBanner);

        currentUserEntity.setCurrentBannerPicURL(filename);
        userService.save(currentUserEntity);

        String publicUrl = "/uploads/public/" + currentUserEntity.getPublicId() + "/banner/" + filename;
        Map<String, Object> response = new HashMap<>();
        response.put("bannerPicUrl", publicUrl);
        response.put("bannerMediaId", profileBanner.getId());
        return ResponseEntity.ok(response);
    }

    @Transactional(rollbackFor = Exception.class)
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) throws IOException {
        UserEntity currentUserEntity = getCurrentUser();

        MediaEntity post = mediaService.findById(postId);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }

        if (!post.getOwner().getId().equals(currentUserEntity.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to delete this post");
        }

        if (post.getPath() != null) {
            Path filePath = Paths.get(post.getPath());
            Files.deleteIfExists(filePath);
        }

        mediaService.deleteById(post.getId());

        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        UserEntity currentUserEntity = getCurrentUser();

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

            Path userDir = Paths.get(baseUploadDir, "users", currentUserEntity.getFolderName());
            Path targetDir = userDir.resolve("posts");
            Files.createDirectories(targetDir);
            targetFile = targetDir.resolve(filename);
            file.transferTo(targetFile.toFile());

            imageUrl = "/uploads/public/" + currentUserEntity.getPublicId() + "/posts/" + filename;
        }

        MediaEntity post = new MediaEntity();
        post.setContent(content);
        post.setPath(targetFile != null ? targetFile.toString() : null);
        post.setOwner(currentUserEntity);
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

        UserEntity currentUserEntity = getCurrentUser();

        List<MediaEntity> allUserMedia = mediaService.findAllByOwner(currentUserEntity);

        return ResponseEntity.status(HttpStatus.OK).body(
                allUserMedia.stream()
                        .map(media -> {
                            MediaRes dto = MediaRes.fromEntity(media);
                            dto.setLikedByCurrentUser(likeService.existsByUserAndMedia(currentUserEntity, media));
                            int commentCount = commentService.countByMedia(media);
                            dto.setCommentCount(commentCount);
                            return dto;
                        })
                        .toList()
        );
    }
}
