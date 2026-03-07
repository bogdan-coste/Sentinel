package com.sentinel.controller;

import com.sentinel.dto.request.CommentReq;
import com.sentinel.dto.response.CommentRes;
import com.sentinel.entity.*;
import com.sentinel.service.CommentService;
import com.sentinel.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class UserCommentController {

    private final CommentService commentService;
    private final MediaService mediaService;

    public UserCommentController(CommentService commentService, MediaService mediaService){
        this.commentService = commentService;
        this.mediaService = mediaService;
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentReq commentDto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        UserEntity userEntity = (UserEntity) auth.getPrincipal();

        MediaEntity media = mediaService.findById(commentDto.getMediaId());
        if (media == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Media not found");
        }

        CommentEntity comment = commentService.addComment(media, userEntity, commentDto.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(CommentRes.fromEntity(comment));
    }

    @GetMapping("/media/{mediaId}")
    public ResponseEntity<?> getCommentsForMedia(@PathVariable Long mediaId) {
        List<CommentEntity> comments = commentService.getCommentsForMedia(mediaId);
        List<CommentRes> commentDtos = comments.stream().
                                                map(CommentRes::fromEntity)
                                                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(commentDtos);
    }
}
