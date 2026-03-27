package com.sentinel.controller;

import com.sentinel.model.*;
import com.sentinel.service.*;
import com.sentinel.utils.TokenGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import com.sentinel.model.UserRegisterSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRegistrationSessionService userRegisterService;
    private final UserLoginSessionService userLoginService;
    private final EmailService emailService;
    private final TokenGenerator tokenGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserFolderService userFolderService;
    private final MediaService mediaService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, UserRegistrationSessionService userRegisterService,
                          UserLoginSessionService userLoginService, EmailService emailService,
                          TokenGenerator tokenGenerator, PasswordEncoder passwordEncoder,
                          UserFolderService userFolderService, MediaService mediaService){
        this.userService = userService;
        this.userRegisterService = userRegisterService;
        this.userLoginService = userLoginService;
        this.emailService = emailService;
        this.tokenGenerator = tokenGenerator;
        this.passwordEncoder = passwordEncoder;
        this.userFolderService = userFolderService;
        this.mediaService = mediaService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDto userDto) {

        User createdUser = userService.registerUser(userDto);
        UserRegisterSession session = userRegisterService.createSessionForUser(createdUser);

        String verificationLink = "http://localhost:5173/verify?token=" + session.getSessionToken();

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", createdUser.getName());
        variables.put("link", verificationLink);

        MailBody mailBody = new MailBody();
        mailBody.setTo(createdUser.getEmail());
        mailBody.setSubject("Verify your email – Sentinel");
        mailBody.setTemplateName("verification-email");
        mailBody.setVariables(variables);

        emailService.sendVerificationEmail(mailBody);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {

        UserRegisterSession session = userRegisterService.findBySessionToken(token);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("code", "invalid", "message", "Invalid verification link."));
        }
        if (session.getExpirationDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of("code", "expired", "message", "Link expired. Please register again."));
        }
        if (session.isAccountVerified()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("code", "already_verified", "message", "Account already verified."));
        }

        User user = session.getUser();

        try {
            userFolderService.createUserFolders(user.getFolderName());
        }catch(IOException e) {
            log.error("Failed to create folder for user: {}, {}", user.getUsername(), user.getFolderName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Map.of("code", "folder_creation_error", "message", "Error creating your account.")));

        }

        UserMediaMapping mapping = new UserMediaMapping();

        user.setLegitimateUser(true);
        userService.save(user);

        session.setAccountVerified(true);
        userRegisterService.save(session);

        return ResponseEntity.ok(Map.of("code", "success", "message", "Email verified! You can now log in."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userDto, HttpServletResponse response){

        User user = userService.findByEmail(userDto.getEmail());

        if(user == null || !passwordEncoder.matches(userDto.getPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password."));
        }

        if (!user.isLegitimateUser()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Please verify your email before logging in."));
        }

        String token = tokenGenerator.generateToken();

        UserLoginSession session = new UserLoginSession();
        session.setSessionToken(token);
        session.setUser(user);
        session.setEmail(userDto.getEmail());
        session.setExpirationDate(LocalDateTime.now().plusMonths(2));

        userLoginService.save(session);

        Cookie cookie = new Cookie("SESSION_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof User user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid principal");
        }

        Long userId = user.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User ID missing");
        }

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found");
        }

        User freshUser = userOpt.get();

        String profilePicUrl = null;
        if (freshUser.getCurrentProfilePicURL() != null && !freshUser.getCurrentProfilePicURL().isEmpty()) {
            profilePicUrl = "/uploads/public/" + freshUser.getPublicId() + "/profile/" + freshUser.getCurrentProfilePicURL();
        }

        String bannerPicUrl = null;
        if (freshUser.getCurrentBannerPicURL() != null && !freshUser.getCurrentBannerPicURL().isEmpty()) {
            bannerPicUrl = "/uploads/public/" + freshUser.getPublicId() + "/banner/" + freshUser.getCurrentBannerPicURL();
        }

        MediaEntity profilePic = mediaService.getCurrentProfilePictureMedia(freshUser);
        MediaEntity profileBanner = mediaService.getCurrentBannerMedia(freshUser);

        Map<String, Object> response = new HashMap<>();
        response.put("id", freshUser.getId());
        response.put("name", freshUser.getName());
        response.put("username", freshUser.getUsername());
        response.put("email", freshUser.getEmail());
        response.put("description", freshUser.getDescription());
        response.put("friendCount", freshUser.getFriendCount());
        response.put("publicId", freshUser.getPublicId());
        response.put("profilePicUrl", profilePicUrl);
        response.put("profilePicMediaId", profilePic != null ? profilePic.getId() : null);
        response.put("bannerPicUrl", bannerPicUrl);
        response.put("bannerMediaId", profileBanner != null ? profileBanner.getId() : null);

        return ResponseEntity.ok(response);
    }
}

