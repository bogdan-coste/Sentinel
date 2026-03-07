package com.sentinel.dto.response;

import com.sentinel.entity.UserEntity;
import lombok.Data;

@Data
public class UserSearchRes {

    private String username;
    private String name;
    private String profilePicUrl;

    public static UserSearchRes fromEntity(UserEntity userEntity){

        UserSearchRes userSearchRes = new UserSearchRes();

        userSearchRes.setName(userEntity.getName());
        userSearchRes.setUsername(userEntity.getUsername());
        if (userEntity.getCurrentProfilePicURL() != null) {
            userSearchRes.setProfilePicUrl("/uploads/public/" + userEntity.getPublicId() + "/profile/" + userEntity.getCurrentProfilePicURL());
        }

        return userSearchRes;
    }
}
