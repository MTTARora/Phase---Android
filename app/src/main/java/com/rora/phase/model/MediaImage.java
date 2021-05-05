package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class MediaImage {
    @SerializedName("_200x112")
    @Expose
    protected String _200x112;
    @SerializedName("_420x236")
    @Expose
    protected String _420x236;
    @SerializedName("_640x360")
    @Expose
    protected String _640x360;
    @SerializedName("_1280x720")
    @Expose
    protected String _1280x720;
    @SerializedName("_1920x1080")
    @Expose
    protected String _1920x1080;
    @SerializedName("_2560x1440")
    @Expose
    protected String _2560x1440;

    public String get_200x112() {
        return _200x112;
    }

    public void set_200x112(String _200x112) {
        this._200x112 = _200x112;
    }

    public String get_420x236() {
        return _420x236;
    }

    public void set_420x236(String _420x236) {
        this._420x236 = _420x236;
    }

    public String get_640x360() {
        return _640x360;
    }

    public void set_640x360(String _640x360) {
        this._640x360 = _640x360;
    }

    public String get_1280x720() {
        return _1280x720;
    }

    public void set_1280x720(String _1280x720) {
        this._1280x720 = _1280x720;
    }

    public String get_1920x1080() {
        return _1920x1080;
    }

    public void set_1920x1080(String _1920x1080) {
        this._1920x1080 = _1920x1080;
    }

    public String get_2560x1440() {
        return _2560x1440;
    }

    public void set_2560x1440(String _2560x1440) {
        this._2560x1440 = _2560x1440;
    }
}
