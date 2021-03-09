package com.rora.phase.model.api;

public class LoginCredentials {
    private String username;
    private String password;

    public LoginCredentials() {
    }

    public LoginCredentials(String email, String password) {
        username = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
