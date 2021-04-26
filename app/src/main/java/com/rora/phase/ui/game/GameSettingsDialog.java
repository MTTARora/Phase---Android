package com.rora.phase.ui.game;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rora.phase.R;
import com.rora.phase.binding.input.virtual_controller.VirtualController;
import com.rora.phase.preferences.PreferenceConfiguration;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.ViewHelper;

import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.Active;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.HideButtons;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.MoveButtons;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.ResizeButtons;

public class GameSettingsDialog extends Dialog {
    public interface OnControllerModeChanged {
        void onChanged(VirtualController.ControllerMode mode);
    }

    private Switch controllerSwitch;
    private ConstraintLayout frameControllerOptions;
    private VirtualController.ControllerMode controllerMode;

    private OnControllerModeChanged onControllerModeChangedListener;

    public GameSettingsDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.game_settings_dialog);
        ViewHelper.setSizePercentageWithScreen(context, findViewById(R.id.frame_game_settings_dialog), 0.6, 0);

        controllerSwitch = findViewById(R.id.controller_game_settings_switch);
        frameControllerOptions = findViewById(R.id.frame_controller_options_setting_dialog);

        setupControllers();
    }

    private void setupControllers() {
        PreferenceConfiguration preferenceConfiguration = PreferenceConfiguration.readPreferences(getContext());

        controllerSwitch.setChecked(preferenceConfiguration.getOnscreenController());
        frameControllerOptions.setVisibility(preferenceConfiguration.getOnscreenController() ? View.VISIBLE : View.GONE);

        controllerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            frameControllerOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            onControllerModeChangedListener.onChanged(isChecked ? Active : HideButtons);
        });

        findViewById(R.id.edit_controller_position_setting_btn).setOnClickListener(v -> {
            if (controllerMode == MoveButtons) {
                controllerMode = Active;
                onControllerModeChangedListener.onChanged(Active);
            } else {
                controllerMode = MoveButtons;
                onControllerModeChangedListener.onChanged(MoveButtons);
            }
        });

        findViewById(R.id.resize_controller_setting_btn).setOnClickListener(v -> {
            if (controllerMode == ResizeButtons) {
                controllerMode = Active;
                onControllerModeChangedListener.onChanged(Active);
            } else {
                controllerMode = ResizeButtons;
                onControllerModeChangedListener.onChanged(ResizeButtons);
            }
        });
    }

    public void setOnControllerOptionChangedListener(OnControllerModeChanged onControllerOptionChangedListener) {
        onControllerModeChangedListener = onControllerOptionChangedListener;
    }

}
