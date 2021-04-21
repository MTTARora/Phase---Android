package com.rora.phase.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.rora.phase.R;
import com.rora.phase.model.UserPlayingData;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final String PREFERENCE_FILE_KEY = "com.rora.phase.PREFERENCE_FILE_KEY";

    private final String USER_NAME_KEY = "user_name";
    private final String USER_TOKEN_KEY = "user_token";
    private final String USER_COMPUTER_KEY = "user_computer";
    private final String USER_PLAY_STATE_KEY = "user_play_state";
    private final String CURRENT_PLAYING_GAME_KEY = "current_playing_game";

    public static SharedPreferencesHelper newInstance(Context context) {
        return new SharedPreferencesHelper(context);
    }

    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    private void saveString(String key, String value) {
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setUserName(String userName) {
        saveString(USER_NAME_KEY, userName);
    }

    public String getUserName() {
        return sharedPreferences.getString(USER_NAME_KEY, "");
    }

    public void setUserToken(String token) {
        saveString(USER_TOKEN_KEY, token);
    }

    public String getUserToken() {
        return sharedPreferences.getString(USER_TOKEN_KEY, "");
    }

    public void saveUserPlayState(String state) {
        saveString(USER_PLAY_STATE_KEY, state);
    }

    public String getUserPlayState() {
        return sharedPreferences.getString(USER_PLAY_STATE_KEY, "");
    }

    public void saveUserComputer(String data) {
        saveString(USER_COMPUTER_KEY, data);
    }

    public String getUserComputer() {
        return sharedPreferences.getString(USER_COMPUTER_KEY, "");
    }

    public void setCurrentGame(String jsonGame) {
        saveString(CURRENT_PLAYING_GAME_KEY, jsonGame);
    }

    public String getCurrentGame() {
        return sharedPreferences.getString(CURRENT_PLAYING_GAME_KEY, "");
    }
}
