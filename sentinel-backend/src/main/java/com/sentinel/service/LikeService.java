package com.sentinel.service;

import com.sentinel.entity.LikeEntity;
import com.sentinel.entity.MediaEntity;
import com.sentinel.entity.UserEntity;
import com.sentinel.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikeService {

    private final LikeRepository repo;

    public LikeService(LikeRepository repo){
        this.repo = repo;
    }

    public boolean existsByUserAndMedia(UserEntity userEntity, MediaEntity media){
        return repo.existsByUserAndMedia(userEntity, media);
    }

    @Transactional(rollbackFor = Exception.class)
    public LikeEntity save(LikeEntity like){
        return repo.save(like);
    }

    public List<LikeEntity> findByMediaId(Long mediaId){
        return repo.findByMediaId(mediaId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserAndMedia(UserEntity userEntity, MediaEntity media){
        repo.deleteByUserAndMedia(userEntity, media);
    }
}
