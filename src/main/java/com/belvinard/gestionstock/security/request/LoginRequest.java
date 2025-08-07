package com.belvinard.gestionstock.security.request;

public class LoginRequest {
    private String email;  // renommé de username en email
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
