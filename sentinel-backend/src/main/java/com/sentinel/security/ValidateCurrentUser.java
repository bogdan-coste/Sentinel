package com.sentinel.security;

import com.sentinel.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ValidateCurrentUser {

    public Optional<UserEntity> getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()){
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();

        if(!(principal instanceof UserEntity userEntity)){
            return Optional.empty();
        }

        return Optional.of(userEntity);
    }

}
