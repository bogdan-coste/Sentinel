package com.sentinel.Validators;

import com.sentinel.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ValidateCurrentUser {

    public Optional<User> getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()){
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();

        if(!(principal instanceof User user)){
            return Optional.empty();
        }

        return Optional.of(user);
    }

}
