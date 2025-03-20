package com.pantheon.redsocial.Atenea.payload;

public class UserSearchResponse {
    private String username;
    private String fullName;
    private String avatarUrl;

    public UserSearchResponse(String username, String fullName, String avatarUrl) {
        this.username = username;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
