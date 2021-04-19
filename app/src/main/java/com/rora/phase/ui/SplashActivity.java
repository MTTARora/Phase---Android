package com.rora.phase.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.ui.settings.auth.AuthActivity;
import com.rora.phase.ui.viewmodel.UserViewModel;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.resetPlayData();

        Intent intent;
        if (userViewModel.isUserLogged()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, AuthActivity.class);
            intent.putExtra(AuthActivity.START_IN_APP_PARAM, false);
        }

        startActivity(intent);
        finish();
    }

}