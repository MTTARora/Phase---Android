package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rora.phase.model.ui.Media;

public class MediaVideo extends Media {
    @SerializedName("_320x180")
    @Expose
    protected String _320x180;

    @SerializedName("_640x360")
    @Expose
    protected String _640x360;

    @SerializedName("_854x480")
    @Expose
    protected String _854x480;

    @SerializedName("_1920x1080")
    @Expose
    protected String _1920x1080;

    public MediaVideo() {
    }

    public MediaVideo(MediaVideo media) {
        if (media == null)
            return;
        this._320x180 = media.get_320x180();
        this._640x360 = media.get_640x360();
        this._854x480 = media.get_854x480();
        this._1920x1080 = media.get_1920x1080();
    }

    public String get_320x180() {
        if (this._320x180 != null && !this._320x180.isEmpty())
            return _320x180;

        return "";
    }

    public void set_320x180(String _320x180) {
        this._320x180 = _320x180;
    }

    public String get_640x360() {
        if (this._640x360 != null && !this._640x360.isEmpty())
            return _640x360;

        return "";
    }

    public void set_640x360(String _640x360) {
        this._640x360 = _640x360;
    }

    public String get_854x480() {
        if (this._854x480 != null && !this._854x480.isEmpty())
            return _854x480;

        return "";
    }

    public void set_854x480(String _854x480) {
        this._854x480 = _854x480;
    }

    public String get_1920x1080() {
        if (this._1920x1080 != null && !this._1920x1080.isEmpty())
            return _1920x1080;

        return "";
    }

    public void set_1920x1080(String _1920x1080) {
        this._1920x1080 = _1920x1080;
    }

    @Override
    public String getAvailableLink() {

        if (this.availableLink != null && !this.availableLink.isEmpty())
            return availableLink;

        if (!get_854x480().isEmpty())
            return get_854x480();

        if (!get_1920x1080().isEmpty())
            return get_1920x1080();

        if (!get_640x360().isEmpty())
            return get_640x360();

        if (!get_320x180().isEmpty())
            return get_320x180();

        return "";

    }

}
