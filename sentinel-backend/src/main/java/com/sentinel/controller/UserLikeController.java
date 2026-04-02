package com.sentinel.controller;

import com.sentinel.controller.base.Controller;
import com.sentinel.security.ValidateCurrentUser;
import com.sentinel.dto.request.LikeReq;
import com.sentinel.dto.response.LikeRes;
import com.sentinel.entity.*;
import com.sentinel.service.LikeService;
import com.sentinel.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class UserLikeController extends Controller {

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
    public ResponseEntity<?> toggleLike(@RequestBody LikeReq likeDto){

        UserEntity currentUserEntity = getCurrentUser();

        MediaEntity media = mediaService.findById(likeDto.getMediaId());
        boolean alreadyExists = likeService.existsByUserAndMedia(currentUserEntity, media);

        int likeCount = media.getLikeCount();

        if(alreadyExists){
            likeService.deleteByUserAndMedia(currentUserEntity, media);
            likeCount--;
        } else{
            LikeEntity like = new LikeEntity();
            like.setUserEntity(currentUserEntity);
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
        List<LikeRes> likeDto = likes.stream().map(LikeRes::fromEntity).toList();
        return ResponseEntity.ok().body(likeDto);
    }
}
