package com.sentinel.model;

import lombok.Data;

@Data
public class UserSearchDto {

    private String username;
    private String name;
    private String profilePicUrl;

    public static UserSearchDto fromEntity(User user){

        UserSearchDto userSearchDto = new UserSearchDto();

        userSearchDto.setName(user.getName());
        userSearchDto.setUsername(user.getUsername());
        if (user.getCurrentProfilePicURL() != null) {
            userSearchDto.setProfilePicUrl("/uploads/public/" + user.getPublicId() + "/profile/" + user.getCurrentProfilePicURL());
        }

        return userSearchDto;
    }
}
