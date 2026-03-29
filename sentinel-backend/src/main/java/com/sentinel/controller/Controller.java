package com.sentinel.controller;

import com.sentinel.Validators.UnauthorizedException;
import com.sentinel.Validators.ValidateCurrentUser;
import com.sentinel.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public abstract class Controller {

    protected final ValidateCurrentUser authValidator;

    public Controller(ValidateCurrentUser authValidator){
        this.authValidator = authValidator;
    }

    protected User getCurrentUser(){
        return authValidator.getAuthenticatedUser().orElseThrow(() -> new UnauthorizedException("Not authenticated."));
    }
}
