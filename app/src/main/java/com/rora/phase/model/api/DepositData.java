package com.rora.phase.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DepositData {
    @SerializedName("product")
    @Expose
    public int product;
    @SerializedName("cash")
    @Expose
    public double amount;
    @SerializedName("type")
    @Expose
    public int type;

    public DepositData(int product, double amount, int type) {
        this.product = product;
        this.amount = amount;
        this.type = type;
    }
}
