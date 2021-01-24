package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Banner {
    @SerializedName("bannerId")
    @Expose
    private String bannerId;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("desc")
    @Expose
    private String desc;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
