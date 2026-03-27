package com.sentinel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_picture")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePicture {

    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String path;

    @Column
    private String likesNumber;

}
