package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Platform {
    @SerializedName("platformId")
    @Expose
    private Integer platformId;
    @SerializedName("platformName")
    @Expose
    private String platformName;
    @SerializedName("imageLoginPlatform")
    @Expose
    private String imageLoginPlatform;
    @SerializedName("imageRememberPlatform")
    @Expose
    private String imageRememberPlatform;

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getImageLoginPlatform() {
        return imageLoginPlatform;
    }

    public void setImageLoginPlatform(String imageLoginPlatform) {
        this.imageLoginPlatform = imageLoginPlatform;
    }

    public String getImageRememberPlatform() {
        return imageRememberPlatform;
    }

    public void setImageRememberPlatform(String imageRememberPlatform) {
        this.imageRememberPlatform = imageRememberPlatform;
    }
}
