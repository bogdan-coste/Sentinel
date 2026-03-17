package com.sentinel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(columnList = "username"),
        @Index(columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /// --- User ID ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// --- Legitimate User ---
    @Column(nullable = false)
    private boolean legitimateUser = false;

    /// --- Directory Name for the User ---
    @Column(unique = true, nullable = false, length = 32)
    private String folderName;

    /// --- Authentication & Identification
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscore")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    /// --- Basic Profile ---
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Name can only contain letters and spaces")
    private String name;
    private String description;

    @ElementCollection
    @CollectionTable(name = "user_external_links", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "link")
    private List<String> externalLinks;
    private String profilePicUrl;

    /// --- User Details ---
    private LocalDate birthDate;
    private GenderType gender;

    /// --- Account Status ---
    private boolean accountDisabledByUser = false;
    private boolean accountDisabledByPlatform = false;

    /// --- Account Timestamps ---
    @CreationTimestamp
    private LocalDateTime registerDate;

    ///  --- Safeguarded Mode ---
    private boolean safeGuardedMode = false;


}
