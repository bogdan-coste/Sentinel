package com.sentinel.service;

import com.sentinel.model.LikeEntity;
import com.sentinel.model.MediaEntity;
import com.sentinel.model.User;
import com.sentinel.repository.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {

    private final LikeRepository repo;

    public LikeService(LikeRepository repo){
        this.repo = repo;
    }

    public boolean existsByUserAndMedia(User user, MediaEntity media){
        return repo.existsByUserAndMedia(user, media);
    }

    public LikeEntity save(LikeEntity like){
        return repo.save(like);
    }

    public List<LikeEntity> findByMediaId(Long mediaId){
        return repo.findByMediaId(mediaId);
    }

    public void deleteByUserAndMedia(User user, MediaEntity media){
        repo.deleteByUserAndMedia(user, media);
    }
}
