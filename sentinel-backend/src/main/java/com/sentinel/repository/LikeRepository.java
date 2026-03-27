package com.sentinel.repository;

import com.sentinel.model.LikeEntity;
import com.sentinel.model.MediaEntity;
import com.sentinel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    List<LikeEntity> findByMediaId(Long mediaId);
    boolean existsByUserAndMedia(User user, MediaEntity media);
    void deleteByUserAndMedia(User user, MediaEntity media);
}
