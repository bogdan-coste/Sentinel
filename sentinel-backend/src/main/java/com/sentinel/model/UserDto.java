package com.sentinel.model;

import com.sentinel.model.User;
import lombok.Data;

@Data
public class UserDto {
    private String publicId;
    private String name;
    private String username;
    private String profilePicUrl;

    public static UserDto fromEntity(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setPublicId(user.getPublicId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        if (user.getCurrentProfilePicURL() != null) {
            dto.setProfilePicUrl("/uploads/public/" + user.getPublicId() + "/profile/" + user.getCurrentProfilePicURL());
        }
        return dto;
    }
}