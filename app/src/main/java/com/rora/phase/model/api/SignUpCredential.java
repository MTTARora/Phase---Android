package com.rora.phase.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpCredential {
    @SerializedName("Email")
    @Expose
    private String username;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("ConfirmPassword")
    @Expose
    private String confirmPw;
    @SerializedName("PhoneNumber")
    @Expose
    private String phoneNumber;

    public SignUpCredential(String username, String password, String confirmPw) {
        this.username = username;
        this.password = password;
        this.confirmPw = confirmPw;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPw() {
        return confirmPw;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
