package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class Wallet {
    @SerializedName("walletId")
    @Expose
    private String id;
    @SerializedName("cash")
    @Expose
    private double cash;
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

    public double getCash() {
        return cash;
    }

    public String getCashInString() {
        return (new DecimalFormat("0.00")).format(cash);
    }

    public void setCash(double cash) {
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
