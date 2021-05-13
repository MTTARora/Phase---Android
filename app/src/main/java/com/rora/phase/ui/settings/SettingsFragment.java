package com.rora.phase.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rora.phase.R;
import com.rora.phase.ui.auth.AuthActivity;

import carbon.widget.Button;

public class SettingsFragment extends Fragment {

    private TextView userNameTv;
    private Button signBtn;

    private SettingsViewModel settingsViewModel;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        userNameTv = root.findViewById(R.id.user_name_edt);
        signBtn = root.findViewById(R.id.sign_out_btn);

        signBtn.setOnClickListener(v -> {
            settingsViewModel.signOut();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.putExtra(AuthActivity.START_IN_APP_PARAM, true);
            startActivity(intent);
        });

        initData();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateData();
    }

    private void initData() {
        updateData();
    }

    private void updateData() {
        signBtn.setText(!settingsViewModel.isUserLogged() ? getResources().getString(R.string.sign_in_text) : getResources().getString(R.string.sign_out_text));
        userNameTv.setText(settingsViewModel.getUserName().isEmpty() ? getResources().getString(R.string.guest_text) : settingsViewModel.getUserName());
    }

}