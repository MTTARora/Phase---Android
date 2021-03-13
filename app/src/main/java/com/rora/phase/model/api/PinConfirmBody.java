package com.rora.phase.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PinConfirmBody {
    @SerializedName("pin")
    @Expose
    private String pin;
    @SerializedName("hostId")
    @Expose
    private String hostId;

    public PinConfirmBody(String pin, String hostId) {
        this.pin = pin;
        this.hostId = hostId;
    }
}
