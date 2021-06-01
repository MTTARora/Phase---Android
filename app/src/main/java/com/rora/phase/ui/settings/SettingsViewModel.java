package com.rora.phase.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.rora.phase.model.User;
import com.rora.phase.repository.UserRepository;

public class SettingsViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application.getApplicationContext());
    }

    public void signOut() {
        userRepository.signOut();
    }

    public boolean isUserLogged() {
        return userRepository.isUserLogged();
    }

    public User getLocalUserInfo() {
        if (userRepository.getLocalUserInfo().isEmpty())
            return null;

        return (new Gson()).fromJson(userRepository.getLocalUserInfo(), User.class);
    }
}