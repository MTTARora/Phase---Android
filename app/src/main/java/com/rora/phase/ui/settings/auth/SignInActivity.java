package com.rora.phase.ui.settings.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.ui.viewmodel.UserViewModel;

public class SignInActivity extends AppCompatActivity {

    private TextView errTv;
    private EditText usernameTv, passwordTv;
    private ProgressBar progressBar;
    private Button signInBtn, guestBtn;

    private UserViewModel userViewModel;
    private boolean fromInApp;

    public static String START_IN_APP_PARAM = "in_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        setContentView(R.layout.activity_sign_in);
        usernameTv = findViewById(R.id.user_name_tv);
        passwordTv = findViewById(R.id.pw_tv);
        errTv = findViewById(R.id.err_sign_in_tv);
        progressBar = findViewById(R.id.sign_in_pb);
        signInBtn = findViewById(R.id.sign_in_btn);
        guestBtn = findViewById(R.id.guest_btn);

        signInBtn.setOnClickListener(v -> signIn());
        guestBtn.setOnClickListener(v -> {
            goNextScreen();
        });

        findViewById(R.id.sign_in_frame).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        });

        initData();
    }

    private void initData() {
        userViewModel.getUpdateDataResult().observe(this, dataResultHelper -> {
            loading(false);

            String err = dataResultHelper.getErrMsg();
            if (err != null) {
                errTv.setText(err);
                return;
            }

            goNextScreen();
        });
    }

    private void signIn() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }

        loading(true);
        errTv.setText("");
        String username = usernameTv.getText().toString();
        String password = passwordTv.getText().toString();

        if(username.isEmpty() || password.isEmpty()) {
            loading(false);
            errTv.setText(getResources().getString(R.string.missing_necessary_input_info_text));
            return;
        }

        userViewModel.signIn(username, password);
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            signInBtn.setText("");
        } else {
            progressBar.setVisibility(View.GONE);
            signInBtn.setText(getResources().getString(R.string.sign_in_text));
        }
    }

    private void goNextScreen() {
        if (getIntent().getBooleanExtra(START_IN_APP_PARAM, true))
            onBackPressed();
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}