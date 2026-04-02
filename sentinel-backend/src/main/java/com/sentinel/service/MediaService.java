package com.sentinel.service;

import com.sentinel.entity.MediaEntity;
import com.sentinel.entity.UserEntity;
import com.sentinel.enums.MediaType;
import com.sentinel.repository.MediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MediaService {

    private final MediaRepository repo;

    public MediaService(MediaRepository repo){
        this.repo = repo;
    }

    @Transactional
    public void save(MediaEntity media){
        repo.save(media);
    }

    @Transactional
    public void deleteByOwnerAndTypeAndFilename(UserEntity userEntity, MediaType type, String filename){
        repo.deleteByOwnerAndTypeAndFilename(userEntity, type, filename);
    }

    @Transactional
    public void deleteById(Long mediaId){
        repo.deleteById(mediaId);
    }

    public MediaEntity findById(Long id){
        return repo.findById(id).orElse(null);
    }

    public MediaEntity findByTypeAndOwner(MediaType type, UserEntity owner) {
        return repo.findByTypeAndOwner(type, owner);
    }

    public List<MediaEntity> findAllByOwner(UserEntity owner){
        return repo.findAllByOwner(owner);
    }

    public MediaEntity getCurrentProfilePictureMedia(UserEntity userEntity) {
        String filename = userEntity.getCurrentProfilePicURL();
        if (filename == null) return null;
        return repo.findByOwnerAndTypeAndFilename(userEntity, MediaType.PROFILE_PICTURE, filename)
                .orElse(null);
    }

    public MediaEntity getCurrentBannerMedia(UserEntity userEntity) {
        String filename = userEntity.getCurrentBannerPicURL();
        if (filename == null) return null;
        return repo.findByOwnerAndTypeAndFilename(userEntity, MediaType.PROFILE_BANNER, filename)
                .orElse(null);
    }

    public Optional<MediaEntity> findByOwnerAndTypeAndFilename(UserEntity userEntity, MediaType type, String filename){
        return repo.findByOwnerAndTypeAndFilename(userEntity, type, filename);
    }
}