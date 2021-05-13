package com.rora.phase.ui.auth;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rora.phase.R;
import com.rora.phase.ui.viewmodel.UserViewModel;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TroubleSigningInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TroubleSigningInFragment extends Fragment {

    private TextView tvForgotPwTitle, tvForgotPwMsgResult, tvEmailVerificationTitle, tvEmailVerificationMsgResult, tvOtherIssuesTitle, tvOtherIssuesMsgResult;
    private EditText edtEmailForgotPW, edtEmailMailVerification, edtEmailOtherIssues, edtDescribeIssue;
    private Button btnSendForgotPw, btnSendVerifyMail, btnSendOtherIssues;
    private ConstraintLayout frameInfoForgotPassword, frameInfoEmailVerification, frameInfoOtherIssues;

    private UserViewModel userViewModel;
    private CountDownTimer requestCDTimer;
    private boolean isRequestAvailable = true;

    public TroubleSigningInFragment() {
        // Required empty public constructor
    }

    public static TroubleSigningInFragment newInstance() {
        TroubleSigningInFragment fragment = new TroubleSigningInFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        View root = inflater.inflate(R.layout.fragment_trouble_signing_in, container, false);

        tvForgotPwTitle = root.findViewById(R.id.forgot_password_title_tv);
        tvForgotPwMsgResult = root.findViewById(R.id.msg_result_forgot_password_tv);
        tvEmailVerificationTitle = root.findViewById(R.id.email_verification_title_tv);
        tvEmailVerificationMsgResult = root.findViewById(R.id.msg_result_email_verification_tv);
        tvOtherIssuesTitle = root.findViewById(R.id.other_issues_title_tv);
        tvOtherIssuesMsgResult = root.findViewById(R.id.msg_result_other_issues_tv);

        frameInfoForgotPassword = root.findViewById(R.id.frame_info_forgot_pw);
        frameInfoEmailVerification = root.findViewById(R.id.frame_info_email_verification);
        frameInfoOtherIssues = root.findViewById(R.id.frame_info_other_issues);

        edtEmailForgotPW = root.findViewById(R.id.email_forgot_password_edt);
        edtEmailMailVerification = root.findViewById(R.id.email_email_verification_edt);
        edtEmailOtherIssues = root.findViewById(R.id.email_other_issues_edt);
        edtDescribeIssue = root.findViewById(R.id.other_issues_describe_edt);

        btnSendForgotPw = root.findViewById(R.id.send_forgot_password_btn);
        btnSendVerifyMail = root.findViewById(R.id.send_email_verification_btn);
        btnSendOtherIssues = root.findViewById(R.id.other_issues_send_btn);

        tvForgotPwTitle.setOnClickListener(v -> {
            hideSoftKeyboard();
            frameInfoForgotPassword.setVisibility(frameInfoForgotPassword.getVisibility() == View.VISIBLE ? GONE : View.VISIBLE);
        });
        tvEmailVerificationTitle.setOnClickListener(v -> {
            hideSoftKeyboard();
            frameInfoEmailVerification.setVisibility(frameInfoEmailVerification.getVisibility() == View.VISIBLE ? GONE : View.VISIBLE);
        });
        tvOtherIssuesTitle.setOnClickListener(v -> {
            hideSoftKeyboard();
            frameInfoOtherIssues.setVisibility(frameInfoOtherIssues.getVisibility() == View.VISIBLE ? GONE : View.VISIBLE);
        });

        btnSendForgotPw.setOnClickListener(v -> sendForgotPw());
        btnSendVerifyMail.setOnClickListener(v -> sendVerifyMail());
        btnSendOtherIssues.setOnClickListener(v -> sendOtherIssues());
        root.findViewById(R.id.back_to_sign_in_btn).setOnClickListener(v -> getActivity().onBackPressed());
        root.findViewById(R.id.frame_trouble_sign_in).setOnClickListener(v -> hideSoftKeyboard());

        initData();

        return root;
    }

    private void initData() {
        userViewModel.getForgotPasswordResult().observe(getViewLifecycleOwner(), dataResponse -> {
            stopRequestTimer();
            if (dataResponse.getMsg() != null) {
                tvForgotPwMsgResult.setTextColor(getContext().getColor(R.color.red));
                tvForgotPwMsgResult.setText(dataResponse.getMsg());
                return;
            }

            tvForgotPwMsgResult.setTextColor(getContext().getColor(R.color.colorText));
            tvForgotPwMsgResult.setText(getResources().getString(R.string.sent_password_reset_link_txt));
        });

        userViewModel.getEmailVerificationResult().observe(getViewLifecycleOwner(), dataResponse -> {
            stopRequestTimer();
            if (dataResponse.getMsg() != null) {
                tvEmailVerificationMsgResult.setTextColor(getContext().getColor(R.color.red));
                tvEmailVerificationMsgResult.setText(dataResponse.getMsg());
                return;
            }

            tvEmailVerificationMsgResult.setTextColor(getContext().getColor(R.color.colorText));
            tvEmailVerificationMsgResult.setText(getResources().getString(R.string.sent_verify_link_txt));
        });
    }

    private void sendForgotPw() {
        startRequestTimer();

        tvEmailVerificationMsgResult.setTextColor(getContext().getColor(R.color.colorText));
        tvEmailVerificationMsgResult.setText(getResources().getString(R.string.fill_in_your_email));
        tvOtherIssuesMsgResult.setTextColor(getContext().getColor(R.color.colorText));
        tvOtherIssuesMsgResult.setText(getResources().getString(R.string.fill_in_your_email));

        String email = edtEmailForgotPW.getText().toString();
        if (email == null || email.isEmpty()) {
            tvForgotPwMsgResult.setTextColor(getContext().getColor(R.color.red));
            tvForgotPwMsgResult.setText(getResources().getString(R.string.fill_in_your_email));

            stopRequestTimer();
            return;
        }

        userViewModel.forgotPassword(email);
    }

    private void sendVerifyMail() {
        startRequestTimer();

        tvForgotPwMsgResult.setTextColor(getContext().getColor(R.color.colorText));
        tvForgotPwMsgResult.setText(getResources().getString(R.string.fill_in_your_email));
        tvOtherIssuesMsgResult.setTextColor(getContext().getColor(R.color.colorText));
        tvOtherIssuesMsgResult.setText(getResources().getString(R.string.fill_in_your_email));

        String email = edtEmailMailVerification.getText().toString();
        if (email == null || email.isEmpty()) {
            tvEmailVerificationMsgResult.setTextColor(getContext().getColor(R.color.red));
            tvEmailVerificationMsgResult.setText(getResources().getString(R.string.fill_in_your_email));

            stopRequestTimer();
            return;
        }

        userViewModel.verifyEmail(email);
    }

    private void sendOtherIssues() {
        startRequestTimer();

        tvForgotPwMsgResult.setTextColor(getContext().getColor(R.color.colorText));
        tvForgotPwMsgResult.setText(getResources().getString(R.string.fill_in_your_email));
        tvEmailVerificationMsgResult.setTextColor(getContext().getColor(R.color.colorText));
        tvEmailVerificationMsgResult.setText(getResources().getString(R.string.fill_in_your_email));

        String email = edtEmailMailVerification.getText().toString();
        if (email == null || email.isEmpty()) {
            tvOtherIssuesMsgResult.setTextColor(getContext().getColor(R.color.red));
            tvOtherIssuesMsgResult.setText(getResources().getString(R.string.fill_in_your_email));

            stopRequestTimer();
            return;
        }
    }

    private void startRequestTimer() {
        if (!isRequestAvailable)
            return;

        long totalSeconds = 30;
        long intervalSeconds = 1;

        isRequestAvailable = false;
        requestCDTimer = new CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getActivity().runOnUiThread(() -> {
                    btnSendForgotPw.setText(millisUntilFinished / 1000 + "s");
                    btnSendVerifyMail.setText(millisUntilFinished / 1000 + "s");
                    btnSendOtherIssues.setText(millisUntilFinished / 1000 + "s");
                });
            }

            @Override
            public void onFinish() {
                isRequestAvailable = true;
                getActivity().runOnUiThread(() -> {
                    btnSendForgotPw.setText(R.string.send_txt);
                    btnSendVerifyMail.setText(R.string.send_txt);
                    btnSendOtherIssues.setText(R.string.send_txt);
                });
            }
        }.start();
    }

    private void stopRequestTimer() {
        requestCDTimer.cancel();
        isRequestAvailable = true;
        btnSendForgotPw.setText(R.string.send_txt);
        btnSendVerifyMail.setText(R.string.send_txt);
        btnSendOtherIssues.setText(R.string.send_txt);
    }

    private void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

}