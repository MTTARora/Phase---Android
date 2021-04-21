package com.rora.phase.ui.settings.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rora.phase.R;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.DataResponse;
import com.rora.phase.utils.Dialog;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SignUpFragment extends Fragment {

    private TextView errTv;
    private EditText usernameEdt, passwordEdt, confirmPasswordEdt;
    private ProgressBar progressBar;
    private Button signUpBtn;
    private CheckBox cbPolicy;

    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);

        usernameEdt = root.findViewById(R.id.user_name_edt);
        passwordEdt = root.findViewById(R.id.password_edt);
        confirmPasswordEdt = root.findViewById(R.id.confirm_password_edt);
        errTv = root.findViewById(R.id.err_sign_up_tv);
        progressBar = root.findViewById(R.id.sign_up_pb);
        cbPolicy = root.findViewById(R.id.policy_cb);
        signUpBtn = root.findViewById(R.id.sign_up_btn);

        signUpBtn.setOnClickListener(v -> signUp());

        root.findViewById(R.id.sign_up_frame).setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return true;
        });

        usernameEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                passwordEdt.requestFocus();
            }
            return true;
        });

        passwordEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                confirmPasswordEdt.requestFocus();
            }
            return true;
        });

        confirmPasswordEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                hideSoftKeyboard();

                confirmPasswordEdt.clearFocus();
                signUp();
            }
            return true;
        });

        root.findViewById(R.id.back_to_sign_in_btn).setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        initData();

        return root;
    }

    private void initData() {
        userViewModel.getSignUpResult().observe(getViewLifecycleOwner(), dataResponse -> {
            loading(false);
            String err = dataResponse.getMsg();
            if (err != null) {
                errTv.setText(err);
                return;
            }

            Dialog.displayNotifyDialog(getActivity(), getResources().getString(R.string.sign_up_success_msg),
                    getResources().getString(R.string.sent_verify_link_txt),
                    null,
                    () -> {
                getActivity().getSupportFragmentManager().popBackStack();
                    });
        });
    }

    private void signUp() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }

        loading(true);
        errTv.setText("");
        String username = usernameEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        String confirmPassword = confirmPasswordEdt.getText().toString();

        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            loading(false);
            errTv.setText(getResources().getString(R.string.missing_necessary_input_info_text));
            return;
        }

        if (!password.equals(confirmPassword)) {
            loading(false);
            errTv.setText(getResources().getString(R.string.confirm_password_missmatch_text));
            return;
        }

        if (!cbPolicy.isChecked()) {
            loading(false);
            errTv.setText(getResources().getString(R.string.terms_and_policy_required_text));
            return;
        }

        userViewModel.signUp(username, password, confirmPassword);
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            signUpBtn.setText("");
        } else {
            progressBar.setVisibility(View.GONE);
            signUpBtn.setText(getResources().getString(R.string.sign_up_text));
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

}