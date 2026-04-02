package com.sentinel.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_media_mappings")
@Data
public class UserMediaMapping {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String publicId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String folderName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

}
