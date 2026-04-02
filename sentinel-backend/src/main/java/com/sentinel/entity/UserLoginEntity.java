package com.sentinel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginEntity {

    ///  --- Login ID ---
    @Id
    @GeneratedValue
    private Long id;

    /// --- Login Token ---
    @Column(unique = true, nullable = false)
    private String sessionToken;

    ///  --- Expiration Date ---
    private LocalDateTime expirationDate = LocalDateTime.now().plusMonths(2);

    ///  --- Email ---
    private String email;

    ///  --- Password ---
    private String password;

    ///  --- Associate User with Login Session ---
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;


}
