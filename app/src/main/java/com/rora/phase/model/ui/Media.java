package com.rora.phase.model.ui;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class Media implements Serializable {
    protected String availableLink;

    public enum Quality {
        LOW,
        MEDIUM,
        HIGH
    }

    public abstract String getAvailableLink(Quality qlt);
}
