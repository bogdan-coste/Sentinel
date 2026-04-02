package com.sentinel.controller;

import com.sentinel.dto.request.UserLoginReq;
import com.sentinel.dto.request.UserRegistrationReq;
import com.sentinel.dto.response.MediaRes;
import com.sentinel.dto.response.UserGuestRes;
import com.sentinel.enums.FriendshipStatus;
import com.sentinel.entity.*;
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
import com.sentinel.entity.UserRegistrationEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
    private final FriendshipService friendshipService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, UserRegistrationSessionService userRegisterService,
                          UserLoginSessionService userLoginService, EmailService emailService,
                          TokenGenerator tokenGenerator, PasswordEncoder passwordEncoder,
                          UserFolderService userFolderService, MediaService mediaService,
                          FriendshipService friendshipService){
        this.userService = userService;
        this.userRegisterService = userRegisterService;
        this.userLoginService = userLoginService;
        this.emailService = emailService;
        this.tokenGenerator = tokenGenerator;
        this.passwordEncoder = passwordEncoder;
        this.userFolderService = userFolderService;
        this.mediaService = mediaService;
        this.friendshipService = friendshipService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@Valid @RequestBody UserRegistrationReq userDto) {

        UserEntity createdUserEntity = userService.registerUser(userDto);
        UserRegistrationEntity session = userRegisterService.createSessionForUser(createdUserEntity);

        String verificationLink = "http://localhost:5173/verify?token=" + session.getSessionToken();

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", createdUserEntity.getName());
        variables.put("link", verificationLink);

        MailEntity mailEntity = new MailEntity();
        mailEntity.setTo(createdUserEntity.getEmail());
        mailEntity.setSubject("Verify your email – Sentinel");
        mailEntity.setTemplateName("verification-email");
        mailEntity.setVariables(variables);

        emailService.sendVerificationEmail(mailEntity);

        return new ResponseEntity<>(createdUserEntity, HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {

        UserRegistrationEntity session = userRegisterService.findBySessionToken(token);
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

        UserEntity userEntity = session.getUserEntity();

        try {
            userFolderService.createUserFolders(userEntity.getFolderName());
        }catch(IOException e) {
            log.error("Failed to create folder for user: {}, {}", userEntity.getUsername(), userEntity.getFolderName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Map.of("code", "folder_creation_error", "message", "Error creating your account.")));

        }

        UserMediaMapping mapping = new UserMediaMapping();

        userEntity.setLegitimateUser(true);
        userService.save(userEntity);

        session.setAccountVerified(true);
        userRegisterService.save(session);

        return ResponseEntity.ok(Map.of("code", "success", "message", "Email verified! You can now log in."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginReq userDto, HttpServletResponse response){

        UserEntity userEntity = userService.findByEmail(userDto.getEmail());

        if(userEntity == null || !passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password."));
        }

        if (!userEntity.isLegitimateUser()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Please verify your email before logging in."));
        }

        String token = tokenGenerator.generateToken();

        UserLoginEntity session = new UserLoginEntity();
        session.setSessionToken(token);
        session.setUserEntity(userEntity);
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
        if (!(principal instanceof UserEntity userEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid principal");
        }

        Long userId = userEntity.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User ID missing");
        }

        Optional<UserEntity> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found");
        }

        UserEntity freshUserEntity = userOpt.get();

        String profilePicUrl = null;
        if (freshUserEntity.getCurrentProfilePicURL() != null && !freshUserEntity.getCurrentProfilePicURL().isEmpty()) {
            profilePicUrl = "/uploads/public/" + freshUserEntity.getPublicId() + "/profile/" + freshUserEntity.getCurrentProfilePicURL();
        }

        String bannerPicUrl = null;
        if (freshUserEntity.getCurrentBannerPicURL() != null && !freshUserEntity.getCurrentBannerPicURL().isEmpty()) {
            bannerPicUrl = "/uploads/public/" + freshUserEntity.getPublicId() + "/banner/" + freshUserEntity.getCurrentBannerPicURL();
        }

        MediaEntity profilePic = mediaService.getCurrentProfilePictureMedia(freshUserEntity);
        MediaEntity profileBanner = mediaService.getCurrentBannerMedia(freshUserEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("id", freshUserEntity.getId());
        response.put("name", freshUserEntity.getName());
        response.put("username", freshUserEntity.getUsername());
        response.put("email", freshUserEntity.getEmail());
        response.put("description", freshUserEntity.getDescription());
        response.put("friendCount", freshUserEntity.getFriendCount());
        response.put("publicId", freshUserEntity.getPublicId());
        response.put("profilePicUrl", profilePicUrl);
        response.put("profilePicMediaId", profilePic != null ? profilePic.getId() : null);
        response.put("bannerPicUrl", bannerPicUrl);
        response.put("bannerMediaId", profileBanner != null ? profileBanner.getId() : null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("searchAccounts")
    public ResponseEntity<?> getSearchResults(@RequestParam String query){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        UserEntity userEntity = (UserEntity) auth.getPrincipal();

        if(userEntity == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<UserEntity> userEntities = userService.searchUsers(query);

        List<Map<String, Object>> response = userEntities.stream().map(userElem ->{
            Map<String, Object> map = new HashMap<>();
            map.put("publicId", userElem.getPublicId());
            if (userElem.getCurrentProfilePicURL() != null) {
                map.put("profilePicUrl", "/uploads/public/" + userElem.getPublicId() + "/profile/" + userElem.getCurrentProfilePicURL());
            } else {
                map.put("profilePicUrl", null);
            }
            map.put("username", userElem.getUsername());
            map.put("name", userElem.getName());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String publicId) {
        Optional<UserEntity> targetOpt = userService.findByPublicId(publicId);
        if (targetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        UserEntity targetUserEntity = targetOpt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity currentUserEntity = (UserEntity) auth.getPrincipal();

        if(currentUserEntity == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (currentUserEntity.getPublicId().equals(publicId)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/profile")
                    .build();
        }

        Optional<FriendshipEntity> friendship = friendshipService.findFriendshipBetween(currentUserEntity, targetUserEntity);

        String friendshipStatus = "NONE";
        boolean isFriend = false;

        if (friendship.isPresent()) {
            friendshipStatus = friendship.get().getStatus().name(); // Extracts "PENDING" or "ACCEPTED"
            isFriend = (friendship.get().getStatus() == FriendshipStatus.ACCEPTED);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("friendshipStatus", friendshipStatus);

        UserGuestRes userInfoDto = UserGuestRes.fromEntity(targetUserEntity, isFriend);
        response.put("userInformation", userInfoDto);

        if(isFriend) {
            List<UserEntity> targetUserEntityFriends = friendshipService.findFriends(targetUserEntity);
            List<UserGuestRes> friendDtos = targetUserEntityFriends.stream()
                    .map(friend -> UserGuestRes.fromEntity(friend, true))
                    .toList();
            List<MediaEntity> targetUserMedia = mediaService.findAllByOwner(targetUserEntity);
            List<MediaRes> mediaDtos = targetUserMedia.stream().
                    map(MediaRes::fromEntity).toList();

            response.put("friends", friendDtos);
            response.put("media", mediaDtos);
        }

        return ResponseEntity.ok(response);
    }
}

