package com.sentinel.controller;

import com.sentinel.controller.base.Controller;
import com.sentinel.entity.UserEntity;
import com.sentinel.security.ValidateCurrentUser;
import com.sentinel.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users/settings")
public class UserSettingsController extends Controller {

    private final UserService userService;

    public UserSettingsController(UserService userService, ValidateCurrentUser validator){
        super(validator);
        this.userService = userService;
    }

}
