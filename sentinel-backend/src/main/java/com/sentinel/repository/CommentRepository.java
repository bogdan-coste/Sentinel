package com.sentinel.repository;

import com.sentinel.entity.CommentEntity;
import com.sentinel.entity.MediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostIdOrderByCreatedAtDesc(Long postId);

    int countByPost(MediaEntity post);
}
