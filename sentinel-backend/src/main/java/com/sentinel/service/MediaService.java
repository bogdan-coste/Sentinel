package com.sentinel.service;

import com.sentinel.model.MediaEntity;
import com.sentinel.model.MediaType;
import com.sentinel.model.User;
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
    public void deleteByOwnerAndTypeAndFilename(User user, MediaType type, String filename){
        repo.deleteByOwnerAndTypeAndFilename(user, type, filename);
    }

    @Transactional
    public void deleteById(Long mediaId){
        repo.deleteById(mediaId);
    }

    public MediaEntity findById(Long id){
        return repo.findById(id).orElse(null);
    }

    public MediaEntity findByTypeAndOwner(MediaType type, User owner) {
        return repo.findByTypeAndOwner(type, owner);
    }

    public List<MediaEntity> findAllByOwner(User owner){
        return repo.findAllByOwner(owner);
    }

    public MediaEntity getCurrentProfilePictureMedia(User user) {
        String filename = user.getCurrentProfilePicURL();
        if (filename == null) return null;
        return repo.findByOwnerAndTypeAndFilename(user, MediaType.PROFILE_PICTURE, filename)
                .orElse(null);
    }

    public MediaEntity getCurrentBannerMedia(User user) {
        String filename = user.getCurrentBannerPicURL();
        if (filename == null) return null;
        return repo.findByOwnerAndTypeAndFilename(user, MediaType.PROFILE_BANNER, filename)
                .orElse(null);
    }

    public Optional<MediaEntity> findByOwnerAndTypeAndFilename(User user, MediaType type, String filename){
        return repo.findByOwnerAndTypeAndFilename(user, type, filename);
    }
}