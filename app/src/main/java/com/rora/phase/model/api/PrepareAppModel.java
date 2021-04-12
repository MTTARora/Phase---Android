package com.rora.phase.model.api;

public class PrepareAppModel {
    public String appId;
    public String platformId;
    public String username;
    public String password;

    public PrepareAppModel(String appId, String platformId, String platformUsername, String platformPassword) {
        this.appId = appId;
        this.platformId = platformId;
        this.username = platformUsername;
        this.password = platformPassword;
    }
}
