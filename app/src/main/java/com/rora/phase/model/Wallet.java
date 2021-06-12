package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wallet {
    @SerializedName("walletId")
    @Expose
    private String id;
    @SerializedName("cash")
    @Expose
    private String cash;
    @SerializedName("totalCredit")
    @Expose
    private String totalCredit;
    @SerializedName("availableCredit")
    @Expose
    private String availableCredit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCash() {
        return cash == null ? "0.00" : cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(String totalCredit) {
        this.totalCredit = totalCredit;
    }

    public String getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(String availableCredit) {
        this.availableCredit = availableCredit;
    }
}
