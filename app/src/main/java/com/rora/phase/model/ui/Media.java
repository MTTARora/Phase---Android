package com.rora.phase.model.ui;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class Media implements Serializable {
    @SerializedName("link")
    @Expose
    protected String availableLink;

    public enum Quality {
        LOW,
        MEDIUM,
        HIGH
    }

    public abstract String getAvailableLink(Quality qlt);

    public void setAvailableLink(String availableLink) {
        this.availableLink = availableLink;
    }
}
