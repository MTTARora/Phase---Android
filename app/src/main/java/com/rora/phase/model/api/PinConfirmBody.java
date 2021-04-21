package com.rora.phase.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PinConfirmBody {
    @SerializedName("pin")
    @Expose
    private String pin;

    public PinConfirmBody(String pin) {
        this.pin = pin;
    }
}
