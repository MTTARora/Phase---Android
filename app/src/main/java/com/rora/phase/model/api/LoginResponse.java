package com.rora.phase.model.api;

import com.rora.phase.model.User;

public class LoginResponse {

    private User info;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(User info, String token) {
        this.info = info;
        this.token = token;
    }

    public User getInfo() {
        return info;
    }

    public void setInfo(User info) {
        this.info = info;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
