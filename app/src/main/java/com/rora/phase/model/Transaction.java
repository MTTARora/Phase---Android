package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class Transaction {
    @SerializedName("transactionId")
    @Expose
    private String id;
    @SerializedName("total")
    @Expose
    private double amount;
    @SerializedName("paymentDate")
    @Expose
    private String date;
    @SerializedName("state")
    @Expose
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountInString() {
        return (new DecimalFormat("#.00")).format(amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
