package com.sentinel.controller;

import com.sentinel.model.User;
import com.sentinel.model.UserRegisterSession;
import com.sentinel.model.UserRegistrationDto;
import com.sentinel.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sentinel.service.UserRegistrationSessionService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRegistrationSessionService userRegistrationSessionService;

    public UserController(UserService userService, UserRegistrationSessionService userRegistrationSessionService){
        this.userService = userService;
        this.userRegistrationSessionService = userRegistrationSessionService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDto userDto){

        User createdUser = userService.registerUser(userDto);
        UserRegisterSession session = userRegistrationSessionService.createSessionForUser(createdUser);
        emailService.sendVerificationEmail(createdUser.getEmail(), session.getSessionToken());

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);

    }

}
