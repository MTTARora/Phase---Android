package com.rora.phase.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupportPlayType {
    @SerializedName("supportPlayTypeId")
    @Expose
    private Integer supportPlayTypeId;
    @SerializedName("supportPlayType")
    @Expose
    private Object supportPlayType;

    public Integer getSupportPlayTypeId() {
        return supportPlayTypeId;
    }

    public void setSupportPlayTypeId(Integer supportPlayTypeId) {
        this.supportPlayTypeId = supportPlayTypeId;
    }

    public Object getSupportPlayType() {
        return supportPlayType;
    }

    public void setSupportPlayType(Object supportPlayType) {
        this.supportPlayType = supportPlayType;
    }
}
