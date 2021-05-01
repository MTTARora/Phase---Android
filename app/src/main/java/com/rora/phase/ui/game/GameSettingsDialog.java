package com.rora.phase.ui.game;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaCodecInfo;
import android.os.Build;
import android.os.Vibrator;
import android.util.Range;
import android.view.Display;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rora.phase.R;
import com.rora.phase.RoraLog;
import com.rora.phase.binding.input.virtual_controller.VirtualController;
import com.rora.phase.binding.video.MediaCodecHelper;
import com.rora.phase.nvstream.jni.MoonBridge;
import com.rora.phase.preferences.GlPreferences;
import com.rora.phase.preferences.PreferenceConfiguration;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.Arrays;

import static android.view.View.GONE;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.Active;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.HideButtons;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.MoveButtons;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.RESET;
import static com.rora.phase.binding.input.virtual_controller.VirtualController.ControllerMode.ResizeButtons;
import static com.rora.phase.preferences.PreferenceConfiguration.AUTOSELECT_H265;
import static com.rora.phase.preferences.PreferenceConfiguration.FORCE_H265_OFF;
import static com.rora.phase.preferences.PreferenceConfiguration.FORCE_H265_ON;
import static com.rora.phase.preferences.PreferenceConfiguration.RES_1080P;
import static com.rora.phase.preferences.PreferenceConfiguration.RES_1440P;
import static com.rora.phase.preferences.PreferenceConfiguration.RES_360P;
import static com.rora.phase.preferences.PreferenceConfiguration.RES_480P;
import static com.rora.phase.preferences.PreferenceConfiguration.RES_4K;
import static com.rora.phase.preferences.PreferenceConfiguration.RES_720P;

public class GameSettingsDialog extends Dialog {
    public interface OnGameSettingsChanged {
        void onResolutionChanged();
        void onFpsChanged();
        void onStretchVideoChanged(boolean isEnable);
        void onAudioChanged();
        void onTouchScreenMethodChanged(boolean enableTrackPad);
        void onMultiControllersChanged();
        void onUsbDriverChanged();
        void onUsbPhaseDriverChanged();
        void onMouseEmulationChanged();
        void onFlipFaceBtnsChanged();
        void onOnlyL3R3Changed();

        void onControllerModeChanged(VirtualController.ControllerMode mode);
        void onVideoFormatChanged();
        void onDropFramesChanged();
    }

    private Activity activity;
    private PreferenceConfiguration prefConfig;

    private SwitchCompat controllerSwitch, touchScreenSwitch, stretchVideoSwitch, rumbleSwitch, rumbleEmulationSwitch;
    private SwitchCompat gamePadDetectionSwitch, mouseNavBtnsSwitch, xb1UsbDriverSwitch, phaseUsbDriverSwitch, mouseEmulationSwitch, flipFaceBtnsSwitch;
    private SwitchCompat l3r3Switch, pipSwitch, neverDropFramesSwitch;
    private TextView currentResolutionTv, currentFpsTv, currentSoundTv, currentVideoFormatTv;
    private ConstraintLayout frameResolution, frameFps, frameSound, frameVideoFormat;
    private ConstraintLayout frameControllerOptions;
    private RadioGroup frameResolutionOptions, frameFpsOptions, frameSoundOptions, frameVideoFormatOptions;
    private VirtualController.ControllerMode controllerMode;

    private int nativeResolutionStartIndex = Integer.MAX_VALUE;

    private OnGameSettingsChanged onSettingsChangedListener;

    public GameSettingsDialog(@NonNull Activity activity) {
        super(activity);
        setContentView(R.layout.game_settings_dialog);
        ViewHelper.setSizePercentageWithScreen(activity, findViewById(R.id.frame_game_settings_dialog), 0.6, 0);

        frameResolution = findViewById(R.id.frame_resolution_game_settings);
        frameFps = findViewById(R.id.frame_fps_game_settings);
        frameSound = findViewById(R.id.frame_sound_game_settings);
        frameVideoFormat = findViewById(R.id.frame_video_format_settings);

        stretchVideoSwitch = findViewById(R.id.stretch_video_game_settings_switch);
        touchScreenSwitch = findViewById(R.id.track_pad_game_settings_switch);
        controllerSwitch = findViewById(R.id.controller_game_settings_switch);
        rumbleSwitch = findViewById(R.id.rumble_game_settings_switch);
        rumbleEmulationSwitch = findViewById(R.id.rumble_emulation_game_settings_switch);
        gamePadDetectionSwitch = findViewById(R.id.gamepad_detection_settings_switch);
        mouseNavBtnsSwitch = findViewById(R.id.mouse_nav_buttons_settings_switch);
        xb1UsbDriverSwitch = findViewById(R.id.xb1_usb_driver_settings_switch);
        phaseUsbDriverSwitch = findViewById(R.id.over_drive_native_xbox_settings_switch);
        mouseEmulationSwitch = findViewById(R.id.mouse_emulation_settings_switch);
        flipFaceBtnsSwitch = findViewById(R.id.flip_face_btns_settings_switch);
        l3r3Switch = findViewById(R.id.only_r3_l3_settings_switch);
        pipSwitch = findViewById(R.id.pip_settings_switch);
        neverDropFramesSwitch = findViewById(R.id.never_drop_frames_settings_switch);

        currentResolutionTv = findViewById(R.id.current_resolution_game_settings_tv);
        currentFpsTv = findViewById(R.id.current_fps_game_settings_tv);
        currentSoundTv = findViewById(R.id.current_sound_game_settings_tv);
        currentVideoFormatTv = findViewById(R.id.current_video_format_settings_tv);

        frameControllerOptions = findViewById(R.id.frame_controller_options_setting_dialog);
        frameResolutionOptions = findViewById(R.id.frame_resolution_options_setting_dialog);
        frameFpsOptions = findViewById(R.id.frame_fps_options_setting_dialog);
        frameSoundOptions = findViewById(R.id.frame_sound_options_setting_dialog);
        frameVideoFormatOptions = findViewById(R.id.frame_video_format_options_seting_dialog);

        this.activity = activity;
        prefConfig = PreferenceConfiguration.readPreferences(activity);

        setupViews();

        configBasicSettings();

        configAudioSettings();

        configInputSettings();

        configOSCSettings();

        configHostSettings();

        configUISettings();

        configAdvancedSettings();
    }

    @Override
    public void show() {
        frameResolutionOptions.setVisibility(GONE);
        frameFpsOptions.setVisibility(GONE);
        frameSoundOptions.setVisibility(GONE);
        super.show();
    }

    public void setupViews() {
        int maxSupportedFps = 0;

        // Hide non-supported resolution/FPS combinations

        Display display = activity.getWindowManager().getDefaultDisplay();

        int maxSupportedResW = 0;

        // Always allow resolutions that are smaller or equal to the active
        // display resolution because decoders can report total non-sense to us.
        // For example, a p201 device reports:
        // AVC Decoder: OMX.amlogic.avc.decoder.awesome
        // HEVC Decoder: OMX.amlogic.hevc.decoder.awesome
        // AVC supported width range: 64 - 384
        // HEVC supported width range: 64 - 544
        for (Display.Mode candidate : display.getSupportedModes()) {
            // Some devices report their dimensions in the portrait orientation
            // where height > width. Normalize these to the conventional width > height
            // arrangement before we process them.

            int width = Math.max(candidate.getPhysicalWidth(), candidate.getPhysicalHeight());
            int height = Math.min(candidate.getPhysicalWidth(), candidate.getPhysicalHeight());

            addNativeResolutionEntry(width, height);

            if ((width >= 3840 || height >= 2160) && maxSupportedResW < 3840) {
                maxSupportedResW = 3840;
            } else if ((width >= 2560 || height >= 1440) && maxSupportedResW < 2560) {
                maxSupportedResW = 2560;
            } else if ((width >= 1920 || height >= 1080) && maxSupportedResW < 1920) {
                maxSupportedResW = 1920;
            }

            if (candidate.getRefreshRate() > maxSupportedFps) {
                maxSupportedFps = (int) candidate.getRefreshRate();
            }
        }

        // This must be called to do runtime initialization before calling functions that evaluate
        // decoder lists.
        MediaCodecHelper.initialize(getContext(), GlPreferences.readPreferences(getContext()).glRenderer);

        MediaCodecInfo avcDecoder = MediaCodecHelper.findProbableSafeDecoder("video/avc", -1);
        MediaCodecInfo hevcDecoder = MediaCodecHelper.findProbableSafeDecoder("video/hevc", -1);

        if (avcDecoder != null) {
            Range<Integer> avcWidthRange = avcDecoder.getCapabilitiesForType("video/avc").getVideoCapabilities().getSupportedWidths();

            RoraLog.info("AVC supported width range: " + avcWidthRange.getLower() + " - " + avcWidthRange.getUpper());

            // If 720p is not reported as supported, ignore all results from this API
            if (avcWidthRange.contains(1280)) {
                if (avcWidthRange.contains(3840) && maxSupportedResW < 3840) {
                    maxSupportedResW = 3840;
                } else if (avcWidthRange.contains(1920) && maxSupportedResW < 1920) {
                    maxSupportedResW = 1920;
                } else if (maxSupportedResW < 1280) {
                    maxSupportedResW = 1280;
                }
            }
        }

        if (hevcDecoder != null) {
            Range<Integer> hevcWidthRange = hevcDecoder.getCapabilitiesForType("video/hevc").getVideoCapabilities().getSupportedWidths();

            RoraLog.info("HEVC supported width range: " + hevcWidthRange.getLower() + " - " + hevcWidthRange.getUpper());

            // If 720p is not reported as supported, ignore all results from this API
            if (hevcWidthRange.contains(1280)) {
                if (hevcWidthRange.contains(3840) && maxSupportedResW < 3840) {
                    maxSupportedResW = 3840;
                } else if (hevcWidthRange.contains(1920) && maxSupportedResW < 1920) {
                    maxSupportedResW = 1920;
                } else if (maxSupportedResW < 1280) {
                    maxSupportedResW = 1280;
                }
            }
        }

        RoraLog.info("Maximum resolution slot: " + maxSupportedResW);

        if (maxSupportedResW != 0) {
            if (maxSupportedResW < 3840) { // 4K is unsupported
                findViewById(R.id.resolution_4k_rb).setVisibility(GONE);
                //setValue(PreferenceConfiguration.RESOLUTION_PREF_STRING, PreferenceConfiguration.RES_1440P);
                resetBitrateToDefault(null, null);
            }
            if (maxSupportedResW < 2560) { // 1440p is unsupported
                findViewById(R.id.resolution_1440p_rb).setVisibility(GONE);
                //setValue(PreferenceConfiguration.RESOLUTION_PREF_STRING, PreferenceConfiguration.RES_1080P);
                resetBitrateToDefault(null, null);
            }
            if (maxSupportedResW < 1920) { // 1080p is unsupported
                findViewById(R.id.resolution_1080p_rb).setVisibility(GONE);
                //setValue(PreferenceConfiguration.RESOLUTION_PREF_STRING, PreferenceConfiguration.RES_720P);
                resetBitrateToDefault(null, null);
            }
            // Never remove 720p
        }

        if (!PreferenceConfiguration.readPreferences(activity).unlockFps) {
            // We give some extra room in case the FPS is rounded down
            if (maxSupportedFps < 118) {
                findViewById(R.id.fps_120_rb).setVisibility(GONE);
                //setValue(PreferenceConfiguration.FPS_PREF_STRING, "90");
                resetBitrateToDefault(null, null);
            }
            if (maxSupportedFps < 88) { // 1080p is unsupported
                findViewById(R.id.fps_90_rb).setVisibility(GONE);
                //setValue(PreferenceConfiguration.FPS_PREF_STRING, "60");
                resetBitrateToDefault(null, null);
            }
            // Never remove 30 FPS or 60 FPS
        }
    }


    //--------- BASIC SETTINGS -----------

    private void configBasicSettings() {
        setupResolution();
        setupFps();
        setupStretchVideo();
    }

    private void setupResolution() {
        currentResolutionTv.setText(prefConfig.getResolutionPixelString());
        switch (prefConfig.getResolution()) {
            case RES_360P:
                frameResolutionOptions.check(R.id.resolution_360p_rb);
                break;
            case RES_480P:
                frameResolutionOptions.check(R.id.resolution_480p_rb);
                break;
            default:
            case RES_720P:
                frameResolutionOptions.check(R.id.resolution_720p_rb);
                break;
            case RES_1080P:
                frameResolutionOptions.check(R.id.resolution_1080p_rb);
                break;
            case RES_1440P:
                frameResolutionOptions.check(R.id.resolution_1440p_rb);
                break;
            case RES_4K:
                frameResolutionOptions.check(R.id.resolution_4k_rb);
                break;
        }

        frameResolution.setOnClickListener(v -> frameResolutionOptions.setVisibility(frameResolutionOptions.getVisibility() == GONE ? View.VISIBLE : GONE));

        frameResolutionOptions.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedRes = prefConfig.getResolution();
            switch (checkedId) {
                case R.id.resolution_360p_rb:
                    selectedRes = RES_360P;
                    break;
                case R.id.resolution_480p_rb:
                    selectedRes = RES_480P;
                    break;
                case R.id.resolution_720p_rb:
                    selectedRes = RES_720P;
                    break;
                case R.id.resolution_1080p_rb:
                    selectedRes = RES_1080P;
                    break;
                case R.id.resolution_1440p_rb:
                    selectedRes = RES_1440P;
                    break;
                case R.id.resolution_4k_rb:
                    selectedRes = RES_4K;
                    break;
            }
            prefConfig.setResolution(selectedRes);
            currentResolutionTv.setText(prefConfig.getResolutionPixelString());

            //UPDATE BITRATE VALUE

            // Detect if this value is the native resolution option
            CharSequence[] values = prefConfig.getDefaultResolutionList();
            boolean isNativeRes = true;
            for (int i = 0; i < values.length; i++) {
                // Look for a match prior to the start of the native resolution entries
                if (selectedRes.equals(values[i].toString()) && i < nativeResolutionStartIndex) {
                    isNativeRes = false;
                    break;
                }
            }

            // If this is native resolution, show the warning dialog
            if (isNativeRes) {
                //com.rora.phase.utils.Dialog.displayDialog(activity, activity.getResources().getString(R.string.title_native_res_dialog),
                //        activity.getResources().getString(R.string.text_native_res_dialog),
                //        false);
            }

            resetBitrateToDefault(selectedRes, null);

            onSettingsChangedListener.onResolutionChanged();
        });
    }

    private void setupFps() {
        currentFpsTv.setText(prefConfig.getFps() + " FPS");
        switch (prefConfig.getFps()) {
            case 30:
                frameFpsOptions.check(R.id.fps_30_rb);
                break;
            case 60:
                frameFpsOptions.check(R.id.fps_60_rb);
                break;
        }

        frameFps.setOnClickListener(v -> frameFpsOptions.setVisibility(frameFpsOptions.getVisibility() == GONE ? View.VISIBLE : GONE));

        frameFpsOptions.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedFps = prefConfig.getFps();
            switch (checkedId) {
                case R.id.fps_30_rb:
                    prefConfig.setFps(30);
                    break;
                case R.id.fps_60_rb:
                    prefConfig.setFps(60);
                    break;
            }
            // Write the new bitrate value
            resetBitrateToDefault(null, String.valueOf(selectedFps));
            currentFpsTv.setText(prefConfig.getFps() + " FPS");
            onSettingsChangedListener.onFpsChanged();
        });
    }

    private void setupStretchVideo() {
        stretchVideoSwitch.setChecked(prefConfig.getStretchVideo());
        stretchVideoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setStretchVideo(isChecked);
            onSettingsChangedListener.onStretchVideoChanged(isChecked);
        });
    }


    //--------- AUDIO SETTINGS ----------

    private void configAudioSettings() {
        if (prefConfig.getAudioConfiguration() == MoonBridge.AUDIO_CONFIGURATION_STEREO) {
            currentSoundTv.setText("Stereo");
            frameSoundOptions.check(R.id.stereo_sound_rb);
        } else if (prefConfig.getAudioConfiguration() == MoonBridge.AUDIO_CONFIGURATION_51_SURROUND) {
            currentSoundTv.setText("5.1 Surround Sound");
            frameSoundOptions.check(R.id.surround_5_1_rb);
        } else if (prefConfig.getAudioConfiguration() == MoonBridge.AUDIO_CONFIGURATION_71_SURROUND) {
            currentSoundTv.setText("7.1 Surround Sound");
            frameSoundOptions.check(R.id.surround_7_1_rb);
        }

        frameSound.setOnClickListener(v -> frameSoundOptions.setVisibility(frameSoundOptions.getVisibility() == GONE ? View.VISIBLE : GONE));

        frameSoundOptions.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.stereo_sound_rb:
                    currentSoundTv.setText("Stereo");
                    prefConfig.setAudioConfiguration("2");
                    break;
                case R.id.surround_5_1_rb:
                    currentSoundTv.setText("5.1 Surround Sound");
                    prefConfig.setAudioConfiguration("51");
                    break;
                case R.id.surround_7_1_rb:
                    currentSoundTv.setText("7.1 Surround Sound");
                    prefConfig.setAudioConfiguration("71");
                    break;
            }

            onSettingsChangedListener.onAudioChanged();
        });
    }


    //-------------- INPUT SETTINGS -------------

    private void configInputSettings() {
        setupTouchScreenMethod();
        setupGamePadDetection();
        setupMouseNavButtons();
        setupXb1UsbGamePadDriver();
        setupUsbUsingPhaseDriver();
        setupMouseEmulation();
        setupRumbleEmulation();
        setupFlipFaceBtns();
    }

    private void setupTouchScreenMethod() {
        // hide on-screen controls category on non touch screen devices
        if (!getContext().getPackageManager().hasSystemFeature("android.hardware.touchscreen")) {
            touchScreenSwitch.setClickable(false);
            return;
        }

        touchScreenSwitch.setChecked(prefConfig.getTouchscreenTrackpad());
        touchScreenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setTouchscreenTrackpad(isChecked);
            onSettingsChangedListener.onTouchScreenMethodChanged(isChecked);
        });
    }

    private void setupGamePadDetection() {
        gamePadDetectionSwitch.setChecked(prefConfig.getMultiControllerDetection());
        gamePadDetectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setMultiController(isChecked);
            onSettingsChangedListener.onMultiControllersChanged();
        });
    }

    private void setupMouseNavButtons() {
        mouseNavBtnsSwitch.setChecked(prefConfig.getMouseNavButtons());
        mouseNavBtnsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> prefConfig.setMouseNavButtons(isChecked));
    }

    private void setupXb1UsbGamePadDriver() {
        xb1UsbDriverSwitch.setChecked(prefConfig.getUsbDriver());
        xb1UsbDriverSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setUsbDriver(isChecked);
            onSettingsChangedListener.onUsbDriverChanged();
        });
    }

    private void setupUsbUsingPhaseDriver() {
        phaseUsbDriverSwitch.setChecked(prefConfig.getBindAllUsb());
        phaseUsbDriverSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setBindAllUsb(isChecked);
            onSettingsChangedListener.onUsbPhaseDriverChanged();
        });
    }

    private void setupMouseEmulation() {
        mouseEmulationSwitch.setChecked(prefConfig.getMouseEmulation());
        mouseEmulationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setMouseEmulation(isChecked);
            onSettingsChangedListener.onMouseEmulationChanged();
        });
    }

    private void setupRumbleEmulation() {
        // Remove the vibration options if the device can't vibrate
        if (!((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator()) {
            rumbleEmulationSwitch.setClickable(false);
        }

        rumbleEmulationSwitch.setChecked(prefConfig.getVibrateFallbackToDevice());
        rumbleEmulationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setVibrateFallbackToDevice(isChecked);
        });
    }

    private void setupFlipFaceBtns() {
        flipFaceBtnsSwitch.setChecked(prefConfig.getFlipFaceButtons());
        flipFaceBtnsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setFlipFaceButtons(isChecked);
            onSettingsChangedListener.onFlipFaceBtnsChanged();
        });
    }


    //------------------- ON-SCREEN CONTROLS (OSC) SETTINGS -----------------

    private void configOSCSettings() {
        setupOSC();
        setupRumble();
        setupL3R3();
    }

    private void setupOSC() {
        // hide on-screen controls category on non touch screen devices
        if (!getContext().getPackageManager().hasSystemFeature("android.hardware.touchscreen")) {
            controllerSwitch.setClickable(false);
            frameControllerOptions.setVisibility(GONE);
            return;
        }

        controllerSwitch.setChecked(prefConfig.getOnscreenController());
        frameControllerOptions.setVisibility(prefConfig.getOnscreenController() ? View.VISIBLE : GONE);

        controllerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setOnscreenController(isChecked);
            frameControllerOptions.setVisibility(isChecked ? View.VISIBLE : GONE);
            onSettingsChangedListener.onControllerModeChanged(isChecked ? Active : HideButtons);
        });

        findViewById(R.id.edit_controller_position_setting_btn).setOnClickListener(v -> {
            controllerMode = controllerMode == MoveButtons ? Active : MoveButtons;
            onSettingsChangedListener.onControllerModeChanged(controllerMode);
        });

        findViewById(R.id.resize_controller_setting_btn).setOnClickListener(v -> {
            controllerMode = controllerMode == ResizeButtons ? Active : ResizeButtons;
            onSettingsChangedListener.onControllerModeChanged(controllerMode);
        });

        findViewById(R.id.reset_controller_setting_btn).setOnClickListener(v -> {
            prefConfig.resetOSCDisplaySetting();
            onSettingsChangedListener.onControllerModeChanged(RESET);
        });
    }

    private void setupRumble() {
        // Remove the vibration options if the device can't vibrate
        if (!((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator()) {
            rumbleSwitch.setClickable(false);
        }

        rumbleSwitch.setChecked(prefConfig.getVibrateOsc());
        rumbleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> prefConfig.setVibrateOsc(isChecked));
    }

    private void setupL3R3() {
        l3r3Switch.setChecked(prefConfig.getOnlyL3R3());
        l3r3Switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setOnlyL3R3(isChecked);
            onSettingsChangedListener.onOnlyL3R3Changed();
        });
    }


    //-------------------- HOST SETTINGS ------------------------

    private void configHostSettings() {

    }


    //------------------------ UI SETTINGS ------------------------

    private void configUISettings() {
        setupPiP();
    }

    private void setupPiP() {
        // Remove PiP mode on devices pre-Oreo, where the feature is not available (some low RAM devices),
        // and on Fire OS where it violates the Amazon App Store guidelines for some reason.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
                !getContext().getPackageManager().hasSystemFeature("android.software.picture_in_picture") ||
                getContext().getPackageManager().hasSystemFeature("com.amazon.software.fireos")) {
            //PreferenceCategory category = (PreferenceCategory) findPreference("category_ui_settings");
            //category.removePreference(findPreference("checkbox_enable_pip"));
            pipSwitch.setClickable(false);
        }

        pipSwitch.setChecked(prefConfig.getPiP());
        pipSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> prefConfig.setEnablePip(isChecked));
    }


    //------------------- ADVANCED SETTINGS ----------------------

    private void configAdvancedSettings() {
        setupVideoFormat();
        setupNeverDropFrames();
    }

    private void setupVideoFormat() {
        switch (prefConfig.getVideoFormatValue(activity)) {
            case AUTOSELECT_H265:
                currentVideoFormatTv.setText(activity.getResources().getString(R.string.h256_if_available_short));
                frameVideoFormatOptions.check(R.id.h265_if_stable_rb);
                break;
            case FORCE_H265_ON:
                currentVideoFormatTv.setText(activity.getResources().getString(R.string.h256_always_short));
                frameVideoFormatOptions.check(R.id.h265_always_rb);
                break;
            case FORCE_H265_OFF:
                currentVideoFormatTv.setText(activity.getResources().getString(R.string.h256_never_short));
                frameVideoFormatOptions.check(R.id.h265_never_rb);
                break;
        }

        frameVideoFormat.setOnClickListener(v -> frameVideoFormatOptions.setVisibility(frameVideoFormatOptions.getVisibility() == GONE ? View.VISIBLE : GONE));

        frameVideoFormatOptions.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.h265_if_stable_rb:
                    currentVideoFormatTv.setText(activity.getResources().getString(R.string.h256_if_available_short));
                    prefConfig.setVideoFormat("auto");
                    break;
                case R.id.h265_always_rb:
                    currentVideoFormatTv.setText(activity.getResources().getString(R.string.h256_always_short));
                    prefConfig.setVideoFormat("forceh265");
                    break;
                case R.id.h265_never_rb:
                    currentVideoFormatTv.setText(activity.getResources().getString(R.string.h256_never_short));
                    prefConfig.setVideoFormat("neverh265");
                    break;
            }

            onSettingsChangedListener.onVideoFormatChanged();
        });
    }

    //NOT IMPLEMENT YET
    private void setupHDR() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Display.HdrCapabilities hdrCaps = display.getHdrCapabilities();

        // We must now ensure our display is compatible with HDR10
        boolean foundHdr10 = false;
        if (hdrCaps != null) {
            // getHdrCapabilities() returns null on Lenovo Lenovo Mirage Solo (vega), Android 8.0
            for (int hdrType : hdrCaps.getSupportedHdrTypes()) {
                if (hdrType == Display.HdrCapabilities.HDR_TYPE_HDR10) {
                    foundHdr10 = true;
                }
            }
        }

        if (!foundHdr10) {
            //RoraLog.info("Excluding HDR toggle based on display capabilities");
            //Remove hdr option here
        }
    }

    private void setupNeverDropFrames() {
        neverDropFramesSwitch.setChecked(!prefConfig.getFrameDrop());
        neverDropFramesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefConfig.setDisableFrameDrop(!isChecked);
            onSettingsChangedListener.onDropFramesChanged();
        });
    }


    //---------------- SUPPORTS ----------------

    private void addNativeResolutionEntry(int nativeWidth, int nativeHeight) {
        CharSequence[] values = prefConfig.getDefaultResolutionList();

        String newName = getContext().getResources().getString(R.string.resolution_prefix_native) + " (" + nativeWidth + "x" + nativeHeight + ")";
        String newValue = nativeWidth + "x" + nativeHeight;

        // Check if the native resolution is already present
        for (CharSequence value : values) {
            if (newValue.equals(value.toString())) {
                // It is present in the default list, so don't add it again
                return;
            }
        }

        CharSequence[] newEntries = Arrays.copyOf(prefConfig.getDefaultResolutionList(), prefConfig.getDefaultResolutionList().length + 1);
        CharSequence[] newValues = Arrays.copyOf(values, values.length + 1);

        // Add the new native option
        newEntries[newEntries.length - 1] = newName;
        newValues[newValues.length - 1] = newValue;

        prefConfig.addResolution(newEntries);

        if (newValues.length - 1 < nativeResolutionStartIndex) {
            nativeResolutionStartIndex = newValues.length - 1;
        }
    }

    private void resetBitrateToDefault(String res, String fps) {
        prefConfig.setBitrate(res, fps);
    }

    //----------------- EVENTS ------------------

    public void setOnSettingsChangedListener(OnGameSettingsChanged onSettingsChangedListener) {
        this.onSettingsChangedListener = onSettingsChangedListener;
    }
}
