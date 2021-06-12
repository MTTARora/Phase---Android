package com.rora.phase.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PinConfirmData {
    @SerializedName("pin")
    @Expose
    private String pin;

    public PinConfirmData(String pin) {
        this.pin = pin;
    }
}
