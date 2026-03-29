package com.sentinel.controller;

import com.sentinel.Validators.ValidateCurrentUser;
import com.sentinel.model.*;
import com.sentinel.service.LikeService;
import com.sentinel.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class UserLikeController extends Controller{

    private final LikeService likeService;
    private final MediaService mediaService;

    public UserLikeController(LikeService likeService, MediaService mediaService,
                              ValidateCurrentUser validator){
        super(validator);
        this.likeService = likeService;
        this.mediaService = mediaService;
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> toggleLike(@RequestBody LikeRequestDto likeDto){

        User currentUser = getCurrentUser();

        MediaEntity media = mediaService.findById(likeDto.getMediaId());
        boolean alreadyExists = likeService.existsByUserAndMedia(currentUser, media);

        int likeCount = media.getLikeCount();

        if(alreadyExists){
            likeService.deleteByUserAndMedia(currentUser, media);
            likeCount--;
        } else{
            LikeEntity like = new LikeEntity();
            like.setUser(currentUser);
            like.setMedia(media);
            likeService.save(like);
            likeCount++;
        }

        media.setLikeCount(likeCount);
        mediaService.save(media);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", !alreadyExists);
        response.put("likeCount", likeCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/media/{mediaId}/likes")
    public ResponseEntity<?> getLikesForMedia(@PathVariable Long mediaId){
        List<LikeEntity> likes = likeService.findByMediaId(mediaId);
        List<LikeResponseDto> likeDto = likes.stream().map(LikeResponseDto::fromEntity).toList();
        return ResponseEntity.ok().body(likeDto);
    }
}
