package com.sentinel.controller;

import com.sentinel.model.*;
import com.sentinel.service.CommentService;
import com.sentinel.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> addComment(@RequestBody CommentRequestDto commentDto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        User user = (User) auth.getPrincipal();

        MediaEntity media = mediaService.findById(commentDto.getMediaId());
        if (media == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Media not found");
        }

        CommentEntity comment = commentService.addComment(media, user, commentDto.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponseDto.fromEntity(comment));
    }

    @GetMapping("/media/{mediaId}")
    public ResponseEntity<?> getCommentsForMedia(@PathVariable Long mediaId) {
        List<CommentEntity> comments = commentService.getCommentsForMedia(mediaId);
        List<CommentResponseDto> commentDtos = comments.stream().
                                                map(CommentResponseDto::fromEntity)
                                                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(commentDtos);
    }
}
