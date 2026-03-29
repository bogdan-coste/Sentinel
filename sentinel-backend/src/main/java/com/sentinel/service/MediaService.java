package com.sentinel.service;

import com.sentinel.model.MediaEntity;
import com.sentinel.model.MediaType;
import com.sentinel.model.User;
import com.sentinel.repository.MediaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    private MediaRepository repo;

    public MediaService(MediaRepository repo){
        this.repo = repo;
    }

    public void save(MediaEntity media){
        repo.save(media);
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

    public void deleteByOwnerAndTypeAndFilename(User user, MediaType type, String filename){
        repo.deleteByOwnerAndTypeAndFilename(user, type, filename);
    }

    public void deleteById(Long mediaId){
        repo.deleteById(mediaId);
    }
}
