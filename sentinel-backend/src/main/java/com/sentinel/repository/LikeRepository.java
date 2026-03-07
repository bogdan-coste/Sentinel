package com.sentinel.repository;

import com.sentinel.entity.LikeEntity;
import com.sentinel.entity.MediaEntity;
import com.sentinel.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    @Query("SELECT l FROM LikeEntity l WHERE l.media.id = :mediaId")
    List<LikeEntity> findByMediaId(@Param("mediaId") Long mediaId);

    @Query("SELECT COUNT(l) > 0 FROM LikeEntity l WHERE l.userEntity = :user AND l.media = :media")
    boolean existsByUserAndMedia(@Param("user") UserEntity user, @Param("media") MediaEntity media);

    @Modifying
    @Transactional
    @Query("DELETE FROM LikeEntity l WHERE l.userEntity = :user AND l.media = :media")
    void deleteByUserAndMedia(@Param("user") UserEntity user, @Param("media") MediaEntity media);
}