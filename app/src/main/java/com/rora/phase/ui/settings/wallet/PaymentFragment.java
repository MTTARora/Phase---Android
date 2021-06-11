package com.rora.phase.ui.settings.wallet;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.rora.phase.R;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.ui.BaseFragment;

public class PaymentFragment extends BaseFragment {

    private EditText amountEdt;
    private WebView webView;

    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment, container, false);
        amountEdt = root.findViewById(R.id.deposit_amount_edt);
        webView = root.findViewById(R.id.deposit_wv);

        root.findViewById(R.id.confirm_deposit_btn).setOnClickListener(v -> deposit());

        setupView(root);
        initData();
        return root;
    }

    private void setupView(View root) {
        showActionbar(root, getString(R.string.wallet_title), true, null);
    }

    private void initData() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void deposit() {
        String amount = amountEdt.getText().toString();

        if (!amount.isEmpty())
            userViewModel.deposit(amount, (errMsg, data) -> {
                if (errMsg != null && !errMsg.isEmpty()) {
                    webView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
                }
                else {
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(data);
                }
            });
    }

}