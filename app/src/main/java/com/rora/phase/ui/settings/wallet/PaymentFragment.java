package com.rora.phase.ui.settings.wallet;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.rora.phase.R;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.SharedPreferencesHelper;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.MoneyValueFilter;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.rora.phase.utils.ui.MoneyValueFilter.getDecimalFormattedString;

public class PaymentFragment extends BaseFragment {

    private EditText amountEdt;
    private WebView webView;
    private RadioButton poliMethod;

    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment, container, false);
        amountEdt = root.findViewById(R.id.deposit_amount_edt);
        webView = root.findViewById(R.id.deposit_wv);
        poliMethod = root.findViewById(R.id.poli_method_options_cb);

        setupView(root);
        initData();
        return root;
    }

    private void setupView(View root) {
        showActionbar(root, getString(R.string.deposit), true, null);
        amountEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                int cursorPosition = amountEdt.getSelectionEnd();
                String originalStr = amountEdt.getText().toString();

                //To restrict only two digits after decimal place
                amountEdt.setFilters(new InputFilter[]{new MoneyValueFilter(2)});

                try {
                    amountEdt.removeTextChangedListener(this);
                    String value = amountEdt.getText().toString();

                    if (value != null && !value.equals("")) {
                        if (value.startsWith(".")) {
                            amountEdt.setText("0.");
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            amountEdt.setText("");
                        }
                        String str = amountEdt.getText().toString().replaceAll(",", "");
                        if (!value.equals(""))
                            amountEdt.setText(getDecimalFormattedString(str));

                        int diff = amountEdt.getText().toString().length() - originalStr.length();
                        amountEdt.setSelection(cursorPosition + diff);
                    }
                    amountEdt.addTextChangedListener(this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    amountEdt.addTextChangedListener(this);
                }
            }
        });

        amountEdt.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                amountEdt.clearFocus();
                return true;
            }
            return false;
        });

        root.findViewById(R.id.confirm_deposit_btn).setOnClickListener(v -> deposit());
        root.findViewById(R.id.one_dollar_btn).setOnClickListener(v -> amountEdt.setText("1"));
        root.findViewById(R.id.ten_dollar_btn).setOnClickListener(v -> amountEdt.setText("10"));
        root.findViewById(R.id.one_hundred_dollar_btn).setOnClickListener(v -> amountEdt.setText("100"));

        root.setOnClickListener(v -> {
            hideSoftKeyboard();
            amountEdt.clearFocus();
        });
    }

    private void initData() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void deposit() {
        String amount = amountEdt.getText().toString();

        if (!amount.isEmpty())
            userViewModel.deposit(amount, 1, poliMethod.isChecked() ? 4 : 4, (errMsg, data) -> {
                if (errMsg != null && !errMsg.isEmpty()) {
                    webView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
                }
                else {

                    webView.setVisibility(View.VISIBLE);
                    webView.setWebViewClient(new WebViewClient() {
                        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest webResourceRequest) {
                            try {

                                OkHttpClient okHttpClient = new OkHttpClient();
                                Request request = new Request.Builder().url(webResourceRequest.getUrl().toString())
                                        .addHeader("Authorization", "Bearer " + SharedPreferencesHelper.newInstance(getContext()).getUserToken())
                                        .build();
                                Response response = okHttpClient.newCall(request).execute();
                                return new WebResourceResponse(response.header("text/html", "application/json"), // You can set something other as default content-type
                                        response.header("content-encoding", "utf-8"),  // Again, you can set another encoding as default
                                        response.body().byteStream());
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                    });
                    webView.loadUrl(data);
                }
            });
    }

}