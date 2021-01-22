package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NumberOfPlayer {

    @SerializedName("numberOfPlayerId")
    @Expose
    private Integer numberOfPlayerId;
    @SerializedName("numberOfPlayer")
    @Expose
    private Object numberOfPlayer;

    public Integer getNumberOfPlayerId() {
        return numberOfPlayerId;
    }

    public void setNumberOfPlayerId(Integer numberOfPlayerId) {
        this.numberOfPlayerId = numberOfPlayerId;
    }

    public Object getNumberOfPlayer() {
        return numberOfPlayer;
    }

    public void setNumberOfPlayer(Object numberOfPlayer) {
        this.numberOfPlayer = numberOfPlayer;
    }

}
