package com.sentinel.service;

import com.sentinel.entity.UserEntity;
import com.sentinel.dto.request.UserRegistrationReq;
import com.sentinel.repository.UserRepository;
import com.sentinel.utils.FolderNameGenerator;
import com.sentinel.utils.PublicIdGenerator;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public UserEntity registerUser(UserRegistrationReq dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setFolderName(generateUniqueFolderName());
        userEntity.setPublicId(generateUniquePublicId());
        userEntity.setEmail(dto.getEmail());
        userEntity.setUsername(dto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setName(dto.getName());
        userEntity.setBirthDate(dto.getBirthDate());
        userEntity.setGender(dto.getGender());
        userEntity.setSafeGuardedMode(false);

        return userRepository.save(userEntity);
    }

    public void save(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserEntity> findByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId);
    }

    public List<UserEntity> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

}