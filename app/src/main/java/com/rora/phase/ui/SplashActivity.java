package com.rora.phase.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.ui.settings.auth.SignInActivity;
import com.rora.phase.ui.viewmodel.UserViewModel;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        Intent intent;
        if (userViewModel.isUserLogged()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, SignInActivity.class);
            intent.putExtra(SignInActivity.START_IN_APP_PARAM, false);
        }

        startActivity(intent);
        finish();
    }

}