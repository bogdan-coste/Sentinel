package com.sentinel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import com.sentinel.utils.TokenGenerator;

@Entity
@Table(name = "user_registration_session", indexes={
        @Index(columnList = "session_token"),
        @Index(columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterSession {


    ///  --- Registration ID ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    ///  --- Registration Time Stamp ---
    @CreationTimestamp
    private LocalDateTime registrationTimeStamp = LocalDateTime.now();

    ///  --- Expiration Date ---
    @Column(nullable = false)
    private LocalDateTime expirationDate = LocalDateTime.now().plusDays(2);

    ///  --- Registration Session Token ---
    @Column(unique = true, nullable = false)
    private String sessionToken;

    ///  --- Token Expired ---
    @Column(nullable = false)
    private boolean tokenExpired = false;

    /// --- Account Verified ---
    @Column(nullable = false)
    private boolean accountVerified = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
