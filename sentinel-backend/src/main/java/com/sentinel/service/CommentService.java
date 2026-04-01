package com.sentinel.service;

import com.sentinel.model.CommentEntity;
import com.sentinel.model.MediaEntity;
import com.sentinel.model.User;
import com.sentinel.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository repo;

    public CommentService(CommentRepository repo){
        this.repo = repo;
    }

    public List<CommentEntity> getCommentsForMedia(Long mediaId) {
        return repo.findByPostIdOrderByCreatedAtDesc(mediaId);
    }

    @Transactional(rollbackFor = Exception.class)
    public CommentEntity addComment(MediaEntity media, User author, String content){
        CommentEntity comment = new CommentEntity();
        comment.setAuthor(author);
        comment.setPost(media);
        comment.setContent(content);
        return repo.save(comment);
    }

    public int countByMedia(MediaEntity media){
        return repo.countByPost(media);
    }
}
