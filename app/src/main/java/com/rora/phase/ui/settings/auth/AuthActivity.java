package com.rora.phase.ui.settings.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.ui.FragmentManagerHelper;

public class AuthActivity extends AppCompatActivity {

    public static String START_IN_APP_PARAM = "in_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        SignInFragment signInFragment = SignInFragment.newInstance(getIntent().getBooleanExtra(START_IN_APP_PARAM, true));
        FragmentManagerHelper.replace(getSupportFragmentManager(), R.id.auth_container, signInFragment, null);
    }

}