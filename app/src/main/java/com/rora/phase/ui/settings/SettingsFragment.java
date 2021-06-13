package com.rora.phase.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.rora.phase.R;
import com.rora.phase.model.User;
import com.rora.phase.model.Wallet;
import com.rora.phase.ui.auth.AuthActivity;
import com.rora.phase.ui.settings.wallet.WalletFragment;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.DataResult;
import com.rora.phase.utils.ui.BaseFragment;

import carbon.widget.Button;

import static android.view.View.GONE;

public class SettingsFragment extends BaseFragment {

    private ConstraintLayout frame;
    private LinearLayout sessionUser;
    private ImageView avatar;
    private TextView userNameTv, userIdTv, walletTv;
    private Button signBtn;

    private UserViewModel userViewModel;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        frame = root.findViewById(R.id.frame_settings);
        sessionUser = root.findViewById(R.id.frame_user_settings);
        avatar = root.findViewById(R.id.user_avatar_imv);
        userNameTv = root.findViewById(R.id.user_name_tv);
        userIdTv = root.findViewById(R.id.user_id_tv);
        walletTv = root.findViewById(R.id.wallet_title_settings_tv);
        signBtn = root.findViewById(R.id.sign_out_btn);

        signBtn.setOnClickListener(v -> {
            userViewModel.signOut();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.putExtra(AuthActivity.START_IN_APP_PARAM, true);
            startActivity(intent);
        });

        root.findViewById(R.id.frame_wallet_settings_tv).setOnClickListener(v -> moveTo(new WalletFragment(), WalletFragment.class.getSimpleName(), false));

        setupViews();
        initData();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();
    }

    private void setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(frame, (v, insets) -> {
            frame.setPadding(0, insets.getSystemWindowInsetTop() + (int) getResources().getDimension(R.dimen.normal_space), 0, (int) getResources().getDimension(R.dimen.normal_space));
            return insets;
        });
    }

    private void initData() {
        if (userViewModel.isUserLogged()) {
            showLoadingScreen();
            userViewModel.getWallet();
        }

        userViewModel.getWalletResult().observe(getViewLifecycleOwner(), result -> {
            Wallet wallet = result.getData();
            if (wallet != null)
                walletTv.setText(wallet.getCashInString() + "$");

            hideLoadingScreen();
        });

        updateData();
    }

    private void updateData() {
        User user = userViewModel.getLocalUserInfo();

        sessionUser.setVisibility(!userViewModel.isUserLogged() ? GONE : View.VISIBLE);
        signBtn.setText(!userViewModel.isUserLogged() ? getResources().getString(R.string.sign_in_text) : getResources().getString(R.string.sign_out_text));
        userNameTv.setText(user != null && !user.getEmail().isEmpty() ? user.getEmail() : getResources().getString(R.string.guest_text));
        userIdTv.setText("ID: " + (user != null && !user.getuId().isEmpty() ? user.getuId() : "--"));
    }

}