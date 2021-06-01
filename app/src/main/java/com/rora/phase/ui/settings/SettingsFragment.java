package com.rora.phase.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;

import com.rora.phase.R;
import com.rora.phase.model.User;
import com.rora.phase.ui.auth.AuthActivity;
import com.rora.phase.utils.ui.BaseFragment;

import carbon.widget.Button;

public class SettingsFragment extends BaseFragment {

    private ConstraintLayout frame;
    private ImageView avatar;
    private TextView userNameTv, userIdTv;
    private Button signBtn;

    private SettingsViewModel settingsViewModel;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        frame = root.findViewById(R.id.frame_settings);
        avatar = root.findViewById(R.id.user_avatar_imv);
        userNameTv = root.findViewById(R.id.user_name_tv);
        userIdTv = root.findViewById(R.id.user_id_tv);
        signBtn = root.findViewById(R.id.sign_out_btn);

        signBtn.setOnClickListener(v -> {
            settingsViewModel.signOut();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.putExtra(AuthActivity.START_IN_APP_PARAM, true);
            startActivity(intent);
        });

        setupViews();
        initData();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateData();
    }

    private void setupViews() {
        //showLoadingScreen();
        ViewCompat.setOnApplyWindowInsetsListener(frame, (v, insets) -> {
            frame.setPadding(0, insets.getSystemWindowInsetTop() + (int) getResources().getDimension(R.dimen.normal_space), 0, (int) getResources().getDimension(R.dimen.normal_space));
            return insets;
        });
    }

    private void initData() {
        updateData();
    }

    private void updateData() {
        User user = settingsViewModel.getLocalUserInfo();

        signBtn.setText(!settingsViewModel.isUserLogged() ? getResources().getString(R.string.sign_in_text) : getResources().getString(R.string.sign_out_text));
        userNameTv.setText(user != null && !user.getEmail().isEmpty() ? user.getEmail() : getResources().getString(R.string.guest_text));
        userIdTv.setText("ID: " + (user != null && !user.getuId().isEmpty() ? user.getuId() : "--"));
    }

}