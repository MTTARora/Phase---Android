package com.rora.phase.ui.settings.wallet;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rora.phase.R;
import com.rora.phase.utils.ui.BaseFragment;

public class WalletFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wallet, container, false);

        root.findViewById(R.id.deposit_btn).setOnClickListener(v -> moveTo(new PaymentFragment(), PaymentFragment.class.getSimpleName(), false));

        setupView(root);
        return root;
    }

    private void setupView(View root) {
        showActionbar(root, getString(R.string.wallet_title), true, null);
    }

}