package com.sentinel.entity;

import com.sentinel.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "friendships", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user1_id", "user2_id"})})
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private UserEntity userEntity1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private UserEntity userEntity2;
}
