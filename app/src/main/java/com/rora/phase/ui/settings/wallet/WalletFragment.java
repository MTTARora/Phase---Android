package com.rora.phase.ui.settings.wallet;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rora.phase.R;
import com.rora.phase.model.Transaction;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.DataResult;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.ListWithNotifyView;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
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

    @Override
    public void onResume() {
        super.onResume();
        if (!userViewModel.isUserLogged())
            getActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userViewModel.getTransactionsResult().setValue(new DataResult<>(null, new ArrayList<>()));
    }

    private void setupView(View root) {
        showActionbar(root, null, true, null);

        ((SwipeRefreshLayout)root.findViewById(R.id.refresh_layout_wallet)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                ((SwipeRefreshLayout)root.findViewById(R.id.refresh_layout_wallet)).setRefreshing(false);
            }
        });
        activityLnv.setupList(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false), new TransactionAdapter());
    }

    private void initData() {
        userViewModel.getWalletResult().observe(getViewLifecycleOwner(), walletDataResult -> {
            if (walletDataResult.getMsg() != null && !walletDataResult.getMsg().isEmpty()) {
                hideLoadingScreen();
                return;
            }

            balanceTv.setText(walletDataResult.getData().getCash() + "$");
            hideLoadingScreen();
        });

        userViewModel.getTransactionsResult().observe(getViewLifecycleOwner(), data -> {
            if (data.getMsg() != null && !data.getMsg().isEmpty()) {
                hideLoadingScreen();
                activityLnv.stopLoading(getString(R.string.undetected_error_from_server));
                return;
            }

            activityLnv.getAdapter(true, data.getData()).bindData(data.getData());
        });

        activityLnv.stopLoading(getString(R.string.nothing_here_msg));
        getData();
    }

    private void getData() {
        showLoadingScreen();
        userViewModel.getWallet();
        userViewModel.getTransactions();
    }

}