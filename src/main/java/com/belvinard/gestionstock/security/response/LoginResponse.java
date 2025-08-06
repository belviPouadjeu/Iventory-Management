package com.belvinard.gestionstock.security.response;

import java.util.List;

public class LoginResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private List<String> roles;

    public LoginResponse(String username, List<String> roles, String accessToken) {
        this.username = username;
        this.roles = roles;
        this.accessToken = accessToken;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    // No setter for tokenType unless you need to customize it

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
