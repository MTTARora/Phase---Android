package com.rora.phase.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rora.phase.R;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;

    private final String PREFERENCE_FILE_KEY = "com.rora.phase.PREFERENCE_FILE_KEY";
    private final String USER_TOKEN_KEY = "user_token";

    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    public void setUserToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USER_TOKEN_KEY, token);
        editor.apply();
    }

    public String getUserToken() {
        return sharedPreferences.getString(USER_TOKEN_KEY, "");
    }

}
