package com.sentinel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "media_entity")
@AllArgsConstructor
@NoArgsConstructor
public class MediaEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String path;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy="post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy="media", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LikeEntity> likes = new ArrayList<>();

    @Column(nullable = false)
    private int likeCount;
}
