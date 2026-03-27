package com.sentinel.service;

import com.sentinel.model.User;
import com.sentinel.model.UserRegistrationDto;
import com.sentinel.repository.UserRepository;
import com.sentinel.utils.FolderNameGenerator;
import com.sentinel.utils.PublicIdGenerator;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FolderNameGenerator folderNameGenerator;
    private final PasswordEncoder passwordEncoder;
    private final PublicIdGenerator publicIdGenerator;

    public UserService(UserRepository userRepository,
                       FolderNameGenerator folderNameGenerator,
                       PasswordEncoder passwordEncoder,
                       PublicIdGenerator publicIdGenerator) {
        this.userRepository = userRepository;
        this.folderNameGenerator = folderNameGenerator;
        this.passwordEncoder = passwordEncoder;
        this.publicIdGenerator = publicIdGenerator;
    }

    public String generateUniqueFolderName() {
        String folderName;
        do {
            folderName = folderNameGenerator.generateUniqueFolderName();
        } while (userRepository.existsByFolderName(folderName));
        return folderName;
    }

    private String generateUniquePublicId() {
        String publicId;
        do {
            publicId = publicIdGenerator.generatePublicId();
        } while (userRepository.existsByPublicId(publicId));
        return publicId;
    }

    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setFolderName(generateUniqueFolderName());
        user.setPublicId(generateUniquePublicId());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setSafeGuardedMode(false);

        return userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId);
    }
}