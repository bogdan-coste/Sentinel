package com.sentinel.repository;

import com.sentinel.model.MediaEntity;
import com.sentinel.model.MediaType;
import com.sentinel.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<MediaEntity, Long> {

    MediaEntity findByTypeAndOwner(MediaType type, User owner);

    List<MediaEntity> findAllByOwner(User owner);

    @Query("SELECT m FROM MediaEntity m WHERE m.owner = :user AND m.type = :type AND m.path LIKE %:filename%")
    Optional<MediaEntity> findByOwnerAndTypeAndFilename(@Param("user") User user, @Param("type") MediaType type, @Param("filename") String filename);

    @Modifying
    @Transactional
    @Query("DELETE FROM MediaEntity m WHERE m.owner = :user AND m.type = :type AND m.path LIKE %:filename%")
    void deleteByOwnerAndTypeAndFilename(@Param("user") User user, @Param("type") MediaType type, @Param("filename") String filename);

    void deleteById(Long mediaId);
}