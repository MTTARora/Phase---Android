package com.rora.phase.ui.settings.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rora.phase.MainActivity;
import com.rora.phase.R;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.DataResponse;
import com.rora.phase.utils.ui.FragmentManagerHelper;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.rora.phase.ui.settings.auth.AuthActivity.START_IN_APP_PARAM;

public class SignInFragment extends Fragment {

    private TextView errTv;
    private EditText usernameTv, passwordTv;
    private ProgressBar progressBar;
    private Button signInBtn;

    private UserViewModel userViewModel;

    private boolean fromInApp;

    Observer<DataResponse> signInResultObserver = dataResponse -> {
        loading(false);
        String err = dataResponse.getMsg();
        if (err != null) {
            errTv.setText(err);
            return;
        }

        goNextScreen();
    };

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance(boolean fromInApp) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        args.putBoolean(START_IN_APP_PARAM, fromInApp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fromInApp = getArguments().getBoolean(START_IN_APP_PARAM);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        View root = inflater.inflate(R.layout.fragment_sign_in, container, false);

        usernameTv = root.findViewById(R.id.user_name_edt);
        passwordTv = root.findViewById(R.id.password_edt);
        errTv = root.findViewById(R.id.err_sign_in_tv);
        progressBar = root.findViewById(R.id.sign_in_pb);
        signInBtn = root.findViewById(R.id.sign_in_btn);

        signInBtn.setOnClickListener(v -> signIn());
        root.findViewById(R.id.guest_btn).setOnClickListener(v -> userViewModel.signInAsGuest());

        usernameTv.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                passwordTv.requestFocus();
            }
            return true;
        });

        passwordTv.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                hideSoftKeyboard();

                passwordTv.clearFocus();
                signIn();
            }
            return true;
        });

        root.findViewById(R.id.sign_in_frame).setOnTouchListener((v, event) -> {
            hideSoftKeyboard();
            return true;
        });

        root.findViewById(R.id.sign_in_trouble_btn).setOnClickListener(v ->  {
            errTv.setText("");
            FragmentManagerHelper.replace(getActivity().getSupportFragmentManager(), R.id.auth_container, TroubleSigningInFragment.newInstance(), TroubleSigningInFragment.class.getSimpleName());
        });

        root.findViewById(R.id.go_to_sign_up_btn).setOnClickListener(v -> {
            errTv.setText("");
            FragmentManagerHelper.replace(getActivity().getSupportFragmentManager(), R.id.auth_container, new SignUpFragment(), SignUpFragment.class.getSimpleName());
        });

        initData();

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        userViewModel.getSignInResult().removeObserver(signInResultObserver);
    }

    private void initData() {
        userViewModel.getSignInResult().observe(getActivity(), signInResultObserver);
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

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void goNextScreen() {
        if (getArguments().getBoolean(START_IN_APP_PARAM, true))
            getActivity().finish();
        else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}