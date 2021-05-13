package com.rora.phase.ui.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rora.phase.R;
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