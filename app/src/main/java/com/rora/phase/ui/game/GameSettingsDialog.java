package com.rora.phase.ui.game;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rora.phase.R;
import com.rora.phase.binding.input.virtual_controller.VirtualController;
import com.rora.phase.preferences.PreferenceConfiguration;
import com.rora.phase.utils.ui.ViewHelper;

import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.Active;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.HideButtons;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.MoveButtons;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.ResizeButtons;

public class GameSettingsDialog extends Dialog {
    public interface OnGameSettingsChanged {
        void onTouchScreenMethodChanged(boolean enableTrackPad);
        void onControllerModeChanged(VirtualController.ControllerMode mode);
    }

    private Switch controllerSwitch, touchScreenSwitch;
    private ConstraintLayout frameControllerOptions;
    private VirtualController.ControllerMode controllerMode;

    private OnGameSettingsChanged onGameSettingsChangedListener;

    public GameSettingsDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.game_settings_dialog);
        ViewHelper.setSizePercentageWithScreen(context, findViewById(R.id.frame_game_settings_dialog), 0.6, 0);

        touchScreenSwitch = findViewById(R.id.track_pad_game_settings_switch);
        controllerSwitch = findViewById(R.id.controller_game_settings_switch);
        frameControllerOptions = findViewById(R.id.frame_controller_options_setting_dialog);

        setupTouchScreenMethod();
        setupControllers();
    }

    private void setupTouchScreenMethod() {
        PreferenceConfiguration preferenceConfiguration = PreferenceConfiguration.readPreferences(getContext());

        touchScreenSwitch.setChecked(preferenceConfiguration.isTouchscreenTrackpad());
        touchScreenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onGameSettingsChangedListener.onTouchScreenMethodChanged(isChecked);
        });
    }

    private void setupControllers() {
        PreferenceConfiguration preferenceConfiguration = PreferenceConfiguration.readPreferences(getContext());

        controllerSwitch.setChecked(preferenceConfiguration.getOnscreenController());
        frameControllerOptions.setVisibility(preferenceConfiguration.getOnscreenController() ? View.VISIBLE : View.GONE);

        controllerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            frameControllerOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            onGameSettingsChangedListener.onControllerModeChanged(isChecked ? Active : HideButtons);
        });

        findViewById(R.id.edit_controller_position_setting_btn).setOnClickListener(v -> {
            if (controllerMode == MoveButtons) {
                controllerMode = Active;
                onGameSettingsChangedListener.onControllerModeChanged(Active);
            } else {
                controllerMode = MoveButtons;
                onGameSettingsChangedListener.onControllerModeChanged(MoveButtons);
            }
        });

        findViewById(R.id.resize_controller_setting_btn).setOnClickListener(v -> {
            if (controllerMode == ResizeButtons) {
                controllerMode = Active;
                onGameSettingsChangedListener.onControllerModeChanged(Active);
            } else {
                controllerMode = ResizeButtons;
                onGameSettingsChangedListener.onControllerModeChanged(ResizeButtons);
            }
        });
    }

    public void setOnControllerOptionChangedListener(OnGameSettingsChanged onControllerOptionChangedListener) {
        onGameSettingsChangedListener = onControllerOptionChangedListener;
    }

}
