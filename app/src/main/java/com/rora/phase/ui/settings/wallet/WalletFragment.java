package com.rora.phase.ui.settings.wallet;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.rora.phase.R;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.ListWithNotifyView;

public class WalletFragment extends BaseFragment {

    private TextView balanceTv;
    private ListWithNotifyView activityLnv;

    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        View root = inflater.inflate(R.layout.fragment_wallet, container, false);
        balanceTv = root.findViewById(R.id.wallet_balance_tv);
        activityLnv = root.findViewById(R.id.wallet_activity_list);

        root.findViewById(R.id.deposit_btn).setOnClickListener(v -> moveTo(new PaymentFragment(), PaymentFragment.class.getSimpleName(), false));

        setupView(root);
        initData();
        return root;
    }

    private void setupView(View root) {
        showActionbar(root, null, true, null);
    }

    private void initData() {
        showLoadingScreen();
        userViewModel.getWalletResult().observe(getViewLifecycleOwner(), walletDataResult -> {
            hideLoadingScreen();

            if (walletDataResult.getMsg() != null && !walletDataResult.getMsg().isEmpty()) {
                return;
            }

            balanceTv.setText(walletDataResult.getData().getCash() + "$");
        });
        activityLnv.stopLoading(getString(R.string.nothing_here_msg));
        userViewModel.getWallet();
    }

}