package com.rora.phase.ui.game;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
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
        void onStretchVideoChanged(boolean isEnable);
        void onTouchScreenMethodChanged(boolean enableTrackPad);
        void onControllerModeChanged(VirtualController.ControllerMode mode);
    }

    private PreferenceConfiguration prefConfig;

    private SwitchCompat controllerSwitch, touchScreenSwitch, stretchVideoSwitch;
    private ConstraintLayout frameControllerOptions;
    private VirtualController.ControllerMode controllerMode;

    private OnGameSettingsChanged onSettingsChangedListener;

    public GameSettingsDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.game_settings_dialog);
        ViewHelper.setSizePercentageWithScreen(context, findViewById(R.id.frame_game_settings_dialog), 0.6, 0);

        stretchVideoSwitch = findViewById(R.id.stretch_video_game_settings_switch);
        touchScreenSwitch = findViewById(R.id.track_pad_game_settings_switch);
        controllerSwitch = findViewById(R.id.controller_game_settings_switch);
        frameControllerOptions = findViewById(R.id.frame_controller_options_setting_dialog);

        prefConfig = PreferenceConfiguration.readPreferences(context);

        setupStretchVideo();
        setupTouchScreenMethod();
        setupControllers();
    }

    private void setupStretchVideo() {
        stretchVideoSwitch.setChecked(prefConfig.getStretchVideo());
        stretchVideoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setStretchVideo(isChecked);
            onSettingsChangedListener.onStretchVideoChanged(isChecked);
        });
    }

    private void setupTouchScreenMethod() {
        touchScreenSwitch.setChecked(prefConfig.getTouchscreenTrackpad());
        touchScreenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setTouchscreenTrackpad(isChecked);
            onSettingsChangedListener.onTouchScreenMethodChanged(isChecked);
        });
    }

    private void setupControllers() {
        controllerSwitch.setChecked(prefConfig.getOnscreenController());
        frameControllerOptions.setVisibility(prefConfig.getOnscreenController() ? View.VISIBLE : View.GONE);

        controllerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setOnscreenController(isChecked);
            frameControllerOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            onSettingsChangedListener.onControllerModeChanged(isChecked ? Active : HideButtons);
        });

        findViewById(R.id.edit_controller_position_setting_btn).setOnClickListener(v -> {
            if (controllerMode == MoveButtons) {
                controllerMode = Active;
                onSettingsChangedListener.onControllerModeChanged(Active);
            } else {
                controllerMode = MoveButtons;
                onSettingsChangedListener.onControllerModeChanged(MoveButtons);
            }
        });

        findViewById(R.id.resize_controller_setting_btn).setOnClickListener(v -> {
            if (controllerMode == ResizeButtons) {
                controllerMode = Active;
                onSettingsChangedListener.onControllerModeChanged(Active);
            } else {
                controllerMode = ResizeButtons;
                onSettingsChangedListener.onControllerModeChanged(ResizeButtons);
            }
        });
    }

    public void setOnSettingsChangedListener(OnGameSettingsChanged onSettingsChangedListener) {
        this.onSettingsChangedListener = onSettingsChangedListener;
    }

}
