package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rora.phase.R;

public class NumberOfPlayer {

    @SerializedName("numberOfPlayerId")
    @Expose
    private Integer numberOfPlayerId;
    @SerializedName("numberOfPlayer")
    @Expose
    private Object numberOfPlayer;

    public enum Type {
        ALL(0),
        SINGLE(1),
        COOP(2),
        MULTI(3);

        private int value;
        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

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
