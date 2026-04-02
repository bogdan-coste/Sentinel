package com.sentinel.controller.base;

import com.sentinel.exception.UnauthorizedException;
import com.sentinel.security.ValidateCurrentUser;
import com.sentinel.entity.UserEntity;

public abstract class Controller {

    protected final ValidateCurrentUser authValidator;

    public Controller(ValidateCurrentUser authValidator){
        this.authValidator = authValidator;
    }

    protected UserEntity getCurrentUser(){
        return authValidator.getAuthenticatedUser().orElseThrow(() -> new UnauthorizedException("Not authenticated."));
    }
}
