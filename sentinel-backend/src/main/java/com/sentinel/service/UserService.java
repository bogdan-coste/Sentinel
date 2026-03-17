package com.sentinel.service;

import com.sentinel.model.User;
import com.sentinel.model.UserRegistrationDto;
import com.sentinel.repository.UserRepository;
import com.sentinel.utils.FolderNameGenerator;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FolderNameGenerator folderNameGenerator;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, FolderNameGenerator folderNameGenerator, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.folderNameGenerator = folderNameGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateUniqueFolderName() {
        String folderName;
        do {
            folderName = folderNameGenerator.generateUniqueFolderName();
        } while (userRepository.existsByFolderName(folderName));
        return folderName;
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
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setSafeGuardedMode(false);

        return userRepository.save(user);
    }

}
