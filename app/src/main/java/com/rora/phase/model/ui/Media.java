package com.rora.phase.model.ui;

import android.os.Parcelable;

import java.io.Serializable;

public abstract class Media implements Serializable {
    protected String availableLink;

    public abstract String getAvailableLink();
}
