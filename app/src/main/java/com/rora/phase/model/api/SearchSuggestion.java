package com.rora.phase.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchSuggestion {
    @SerializedName("gameId")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("banner")
    @Expose
    private String logo;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
