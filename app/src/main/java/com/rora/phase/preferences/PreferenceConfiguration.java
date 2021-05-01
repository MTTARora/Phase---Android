package com.rora.phase.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.rora.phase.nvstream.jni.MoonBridge;

import java.util.ArrayList;
import java.util.List;

import static com.rora.phase.binding.input.virtual_controller.VirtualControllerConfigurationLoader.OSC_PREFERENCE;

public class PreferenceConfiguration {
    private static final String LEGACY_RES_FPS_PREF_STRING = "list_resolution_fps";
    private static final String LEGACY_ENABLE_51_SURROUND_PREF_STRING = "checkbox_51_surround";

    static final String RESOLUTION_PREF_STRING = "list_resolution";
    static final String FPS_PREF_STRING = "list_fps";
    static final String BITRATE_PREF_STRING = "seekbar_bitrate_kbps";
    private static final String BITRATE_PREF_OLD_STRING = "seekbar_bitrate";
    private static final String STRETCH_PREF_STRING = "checkbox_stretch_video";
    private static final String SOPS_PREF_STRING = "checkbox_enable_sops";
    private static final String DISABLE_TOASTS_PREF_STRING = "checkbox_disable_warnings";
    private static final String HOST_AUDIO_PREF_STRING = "checkbox_host_audio";
    private static final String DEADZONE_PREF_STRING = "seekbar_deadzone";
    private static final String OSC_OPACITY_PREF_STRING = "seekbar_osc_opacity";
    private static final String LANGUAGE_PREF_STRING = "list_languages";
    private static final String SMALL_ICONS_PREF_STRING = "checkbox_small_icon_mode";
    private static final String MULTI_CONTROLLER_PREF_STRING = "checkbox_multi_controller";
    static final String AUDIO_CONFIG_PREF_STRING = "list_audio_config";
    private static final String USB_DRIVER_PREF_SRING = "checkbox_usb_driver";
    private static final String VIDEO_FORMAT_PREF_STRING = "video_format";
    private static final String ONSCREEN_CONTROLLER_PREF_STRING = "checkbox_show_onscreen_controls";
    private static final String ONLY_L3_R3_PREF_STRING = "checkbox_only_show_L3R3";
    private static final String DISABLE_FRAME_DROP_PREF_STRING = "checkbox_disable_frame_drop";
    private static final String ENABLE_HDR_PREF_STRING = "checkbox_enable_hdr";
    private static final String ENABLE_PIP_PREF_STRING = "checkbox_enable_pip";
    private static final String ENABLE_PERF_OVERLAY_STRING = "checkbox_enable_perf_overlay";
    private static final String BIND_ALL_USB_STRING = "checkbox_usb_bind_all";
    private static final String MOUSE_EMULATION_STRING = "checkbox_mouse_emulation";
    private static final String MOUSE_NAV_BUTTONS_STRING = "checkbox_mouse_nav_buttons";
    static final String UNLOCK_FPS_STRING = "checkbox_unlock_fps";
    private static final String VIBRATE_OSC_PREF_STRING = "checkbox_vibrate_osc";
    private static final String VIBRATE_FALLBACK_PREF_STRING = "checkbox_vibrate_fallback";
    private static final String FLIP_FACE_BUTTONS_PREF_STRING = "checkbox_flip_face_buttons";
    private static final String TOUCHSCREEN_TRACKPAD_PREF_STRING = "checkbox_touchscreen_trackpad";
    private static final String LATENCY_TOAST_PREF_STRING = "checkbox_enable_post_stream_toast";

    static final String DEFAULT_RESOLUTION = "1280x720";
    static final String DEFAULT_FPS = "60";
    private static final boolean DEFAULT_STRETCH = false;
    private static final boolean DEFAULT_SOPS = true;
    private static final boolean DEFAULT_DISABLE_TOASTS = false;
    private static final boolean DEFAULT_HOST_AUDIO = false;
    private static final int DEFAULT_DEADZONE = 15;
    private static final int DEFAULT_OPACITY = 90;
    public static final String DEFAULT_LANGUAGE = "default";
    private static final boolean DEFAULT_MULTI_CONTROLLER = true;
    private static final boolean DEFAULT_USB_DRIVER = true;
    private static final String DEFAULT_VIDEO_FORMAT = "auto";
    private static final boolean ONSCREEN_CONTROLLER_DEFAULT = false;
    private static final boolean ONLY_L3_R3_DEFAULT = false;
    private static final boolean DEFAULT_DISABLE_FRAME_DROP = false;
    private static final boolean DEFAULT_ENABLE_HDR = false;
    private static final boolean DEFAULT_ENABLE_PIP = false;
    private static final boolean DEFAULT_ENABLE_PERF_OVERLAY = false;
    private static final boolean DEFAULT_BIND_ALL_USB = false;
    private static final boolean DEFAULT_MOUSE_EMULATION = true;
    private static final boolean DEFAULT_MOUSE_NAV_BUTTONS = false;
    private static final boolean DEFAULT_UNLOCK_FPS = false;
    private static final boolean DEFAULT_VIBRATE_OSC = true;
    private static final boolean DEFAULT_VIBRATE_FALLBACK = false;
    private static final boolean DEFAULT_FLIP_FACE_BUTTONS = false;
    private static final boolean DEFAULT_TOUCHSCREEN_TRACKPAD = true;
    private static final String DEFAULT_AUDIO_CONFIG = "2"; // Stereo
    private static final boolean DEFAULT_LATENCY_TOAST = false;

    public static final int FORCE_H265_ON = -1;
    public static final int AUTOSELECT_H265 = 0;
    public static final int FORCE_H265_OFF = 1;

    public static final String RES_360P = "640x360";
    public static final String RES_480P = "854x480";
    public static final String RES_720P = "1280x720";
    public static final String RES_1080P = "1920x1080";
    public static final String RES_1440P = "2560x1440";
    public static final String RES_4K = "3840x2160";
    public static final String RES_NATIVE = "Native";

    private Context context;
    private int width, height, fps;
    private int bitrate;
    public int videoFormat;
    public int deadzonePercentage;
    public int oscOpacity;
    private boolean stretchVideo;
    public boolean enableSops, playHostAudio, disableWarnings;
    public String language;
    private boolean smallIconMode, multiController, usbDriver, flipFaceButtons;
    private boolean onscreenController;
    private boolean onlyL3R3;
    private boolean disableFrameDrop;
    public boolean enableHdr;
    private boolean enablePip;
    public boolean enablePerfOverlay;
    public boolean enableLatencyToast;
    private boolean bindAllUsb;
    private boolean mouseEmulation;
    private boolean mouseNavButtons;
    public boolean unlockFps;
    private boolean vibrateOsc;
    private boolean vibrateFallbackToDevice;
    private boolean touchscreenTrackpad;
    private MoonBridge.AudioConfiguration audioConfiguration;

    private CharSequence[] resolutionList = new CharSequence[6];

    public PreferenceConfiguration() {
        resolutionList[0] = RES_360P;
        resolutionList[1] = RES_480P;
        resolutionList[2] = RES_720P;
        resolutionList[3] = RES_1080P;
        resolutionList[4] = RES_1440P;
        resolutionList[5] = RES_4K;
    }

    public PreferenceConfiguration(Context context) {
        this.context = context;
        resolutionList[0] = RES_360P;
        resolutionList[1] = RES_480P;
        resolutionList[2] = RES_720P;
        resolutionList[3] = RES_1080P;
        resolutionList[4] = RES_1440P;
        resolutionList[5] = RES_4K;
    }

    public static boolean isNativeResolution(int width, int height) {
        // It's not a native resolution if it matches an existing resolution option
        if (width == 640 && height == 360) {
            return false;
        }
        else if (width == 854 && height == 480) {
            return false;
        }
        else if (width == 1280 && height == 720) {
            return false;
        }
        else if (width == 1920 && height == 1080) {
            return false;
        }
        else if (width == 2560 && height == 1440) {
            return false;
        }
        else if (width == 3840 && height == 2160) {
            return false;
        }

        return true;
    }

    private static String convertFromLegacyResolutionString(String resString) {
        if (resString.equalsIgnoreCase("360p")) {
            return RES_360P;
        }
        else if (resString.equalsIgnoreCase("480p")) {
            return RES_480P;
        }
        else if (resString.equalsIgnoreCase("720p")) {
            return RES_720P;
        }
        else if (resString.equalsIgnoreCase("1080p")) {
            return RES_1080P;
        }
        else if (resString.equalsIgnoreCase("1440p")) {
            return RES_1440P;
        }
        else if (resString.equalsIgnoreCase("4K")) {
            return RES_4K;
        }
        else {
            // Should be unreachable
            return RES_720P;
        }
    }

    private static int getWidthFromResolutionString(String resString) {
        return Integer.parseInt(resString.split("x")[0]);
    }

    private static int getHeightFromResolutionString(String resString) {
        return Integer.parseInt(resString.split("x")[1]);
    }

    private static String getResolutionString(int width, int height) {
        switch (height) {
            case 360:
                return RES_360P;
            case 480:
                return RES_480P;
            default:
            case 720:
                return RES_720P;
            case 1080:
                return RES_1080P;
            case 1440:
                return RES_1440P;
            case 2160:
                return RES_4K;
        }
    }

    public String getResolution() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(RESOLUTION_PREF_STRING, DEFAULT_RESOLUTION);
    }

    public CharSequence[] getDefaultResolutionList() {
        return resolutionList;
    }

    public void addResolution(CharSequence[] resolutions) {
        resolutionList = resolutions;
    }

    public String getResolutionPixelString() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        switch (prefs.getString(RESOLUTION_PREF_STRING, DEFAULT_RESOLUTION)) {
            case RES_360P:
                return "360p";
            case RES_480P:
                return "480p";
            default:
            case RES_720P:
                return "720p";
            case RES_1080P:
                return "1080p";
            case RES_1440P:
                return "1440p";
            case RES_4K:
                return "4k";
        }
    }

    public void setResolution (String resolution) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(RESOLUTION_PREF_STRING, resolution).apply();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFps() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return fps = Integer.parseInt(prefs.getString(FPS_PREF_STRING, PreferenceConfiguration.DEFAULT_FPS));
    }

    public void setFps(int fps) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(FPS_PREF_STRING, "" + fps).apply();
    }

    public MoonBridge.AudioConfiguration getAudioConfiguration() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String audioConfig = prefs.getString(AUDIO_CONFIG_PREF_STRING, DEFAULT_AUDIO_CONFIG);
        if (audioConfig.equals("71")) {
            audioConfiguration = MoonBridge.AUDIO_CONFIGURATION_71_SURROUND;
        }
        else if (audioConfig.equals("51")) {
            audioConfiguration = MoonBridge.AUDIO_CONFIGURATION_51_SURROUND;
        }
        else /* if (audioConfig.equals("2")) */ {
            audioConfiguration = MoonBridge.AUDIO_CONFIGURATION_STEREO;
        }

        return audioConfiguration;
    }

    public void setAudioConfiguration(String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(AUDIO_CONFIG_PREF_STRING, value).apply();
    }

    public boolean getStretchVideo() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        stretchVideo = prefs.getBoolean(STRETCH_PREF_STRING, DEFAULT_STRETCH);
        return stretchVideo;
    }

    public void setStretchVideo(boolean stretchVideo) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(STRETCH_PREF_STRING, stretchVideo).apply();
    }

    public boolean getOnscreenController() {
        return true;
    }

    public void setOnscreenController(boolean onscreenController) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //Temporary always trigger OSC
        prefs.edit().putBoolean(ONSCREEN_CONTROLLER_PREF_STRING, true).apply();
    }

    public void resetOSCDisplaySetting() {
        context.getSharedPreferences(OSC_PREFERENCE, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public boolean getTouchscreenTrackpad() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return touchscreenTrackpad = prefs.getBoolean(TOUCHSCREEN_TRACKPAD_PREF_STRING, DEFAULT_TOUCHSCREEN_TRACKPAD);
    }

    public void setTouchscreenTrackpad(boolean touchscreenTrackpad) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(TOUCHSCREEN_TRACKPAD_PREF_STRING, touchscreenTrackpad).apply();
    }

    public boolean getVibrateOsc() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return vibrateOsc = prefs.getBoolean(VIBRATE_OSC_PREF_STRING, DEFAULT_VIBRATE_OSC);
    }

    public void setVibrateOsc(boolean vibrateOsc) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(VIBRATE_OSC_PREF_STRING, vibrateOsc).apply();
    }

    public boolean getVibrateFallbackToDevice() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return vibrateFallbackToDevice = prefs.getBoolean(VIBRATE_FALLBACK_PREF_STRING, DEFAULT_VIBRATE_FALLBACK);
    }

    public void setVibrateFallbackToDevice(boolean vibrateFallbackToDevice) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(VIBRATE_FALLBACK_PREF_STRING, vibrateFallbackToDevice).apply();
    }

    public boolean getMultiControllerDetection() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return multiController = prefs.getBoolean(MULTI_CONTROLLER_PREF_STRING, DEFAULT_MULTI_CONTROLLER);
    }

    public void setMultiController(boolean enableMultiController) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(MULTI_CONTROLLER_PREF_STRING, enableMultiController).apply();
    }

    public boolean getSmallIconMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return smallIconMode = prefs.getBoolean(SMALL_ICONS_PREF_STRING, getDefaultSmallMode(context));
    }

    public void setSmallIconMode(boolean enableSmallIconMode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(SMALL_ICONS_PREF_STRING, enableSmallIconMode).apply();
    }

    public boolean getUsbDriver() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return usbDriver = prefs.getBoolean(USB_DRIVER_PREF_SRING, DEFAULT_USB_DRIVER);
    }

    public void setUsbDriver(boolean enableUsbDriver) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(USB_DRIVER_PREF_SRING, enableUsbDriver).apply();
    }

    public boolean getMouseNavButtons() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mouseNavButtons = prefs.getBoolean(MOUSE_NAV_BUTTONS_STRING, DEFAULT_MOUSE_NAV_BUTTONS);
    }

    public void setMouseNavButtons(boolean enableMouseNavButtons) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(MOUSE_NAV_BUTTONS_STRING, enableMouseNavButtons).apply();
    }

    public boolean getBindAllUsb() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return bindAllUsb = prefs.getBoolean(BIND_ALL_USB_STRING, DEFAULT_BIND_ALL_USB);
    }

    public void setBindAllUsb(boolean enableBindAllUsb) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(BIND_ALL_USB_STRING, enableBindAllUsb).apply();
    }

    public boolean getMouseEmulation() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mouseEmulation = prefs.getBoolean(MOUSE_EMULATION_STRING, DEFAULT_MOUSE_EMULATION);
    }

    public void setMouseEmulation(boolean enableMouseEmulation) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(MOUSE_EMULATION_STRING, enableMouseEmulation).apply();
    }

    public boolean getFlipFaceButtons() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return flipFaceButtons = prefs.getBoolean(FLIP_FACE_BUTTONS_PREF_STRING, DEFAULT_FLIP_FACE_BUTTONS);
    }

    public void setFlipFaceButtons(boolean enableFlipFaceButtons) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(FLIP_FACE_BUTTONS_PREF_STRING, enableFlipFaceButtons).apply();
    }

    public boolean getOnlyL3R3() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return onlyL3R3 = prefs.getBoolean(ONLY_L3_R3_PREF_STRING, ONLY_L3_R3_DEFAULT);
    }

    public void setOnlyL3R3(boolean enableL3R3) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(ONLY_L3_R3_PREF_STRING, enableL3R3).apply();
    }

    public boolean getPiP() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return enablePip = prefs.getBoolean(ENABLE_PIP_PREF_STRING, DEFAULT_ENABLE_PIP);
    }

    public void setEnablePip(boolean enablePiP) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(ENABLE_PIP_PREF_STRING, enablePiP).apply();
    }

    public boolean getFrameDrop() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return disableFrameDrop = prefs.getBoolean(DISABLE_FRAME_DROP_PREF_STRING, DEFAULT_DISABLE_FRAME_DROP);
    }

    public void setDisableFrameDrop(boolean disableFrameDrop) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(DISABLE_FRAME_DROP_PREF_STRING, disableFrameDrop).apply();
    }

    public static int getDefaultBitrate(String resString, String fpsString) {
        int width = getWidthFromResolutionString(resString);
        int height = getHeightFromResolutionString(resString);
        int fps = Integer.parseInt(fpsString);

        // This table prefers 16:10 resolutions because they are
        // only slightly more pixels than the 16:9 equivalents, so
        // we don't want to bump those 16:10 resolutions up to the
        // next 16:9 slot.
        //
        // This logic is shamelessly stolen from Moonlight Qt:
        // https://github.com/moonlight-stream/moonlight-qt/blob/master/app/settings/streamingpreferences.cpp

        if (width * height <= 640 * 360) {
            return (int)(1000 * (fps / 30.0));
        }
        else if (width * height <= 854 * 480) {
            return (int)(1500 * (fps / 30.0));
        }
        // This covers 1280x720 and 1280x800 too
        else if (width * height <= 1366 * 768) {
            return (int)(5000 * (fps / 30.0));
        }
        else if (width * height <= 1920 * 1200) {
            return (int)(10000 * (fps / 30.0));
        }
        else if (width * height <= 2560 * 1600) {
            return (int)(20000 * (fps / 30.0));
        }
        else /* if (width * height <= 3840 * 2160) */ {
            return (int)(40000 * (fps / 30.0));
        }
    }

    public int getBitrate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return bitrate = prefs.getInt(BITRATE_PREF_STRING, getDefaultBitrate(prefs.getString(PreferenceConfiguration.RESOLUTION_PREF_STRING, PreferenceConfiguration.DEFAULT_RESOLUTION), prefs.getString(PreferenceConfiguration.FPS_PREF_STRING, PreferenceConfiguration.DEFAULT_FPS)));
    }

    public void setBitrate(String res, String fps) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (res == null) {
            res = prefs.getString(PreferenceConfiguration.RESOLUTION_PREF_STRING, PreferenceConfiguration.DEFAULT_RESOLUTION);
        }
        if (fps == null) {
            fps = prefs.getString(PreferenceConfiguration.FPS_PREF_STRING, PreferenceConfiguration.DEFAULT_FPS);
        }

        this.bitrate = PreferenceConfiguration.getDefaultBitrate(res, fps);

        prefs.edit().putInt(PreferenceConfiguration.BITRATE_PREF_STRING, bitrate).apply();
    }

    public static boolean getDefaultSmallMode(Context context) {
        PackageManager manager = context.getPackageManager();
        if (manager != null) {
            // TVs shouldn't use small mode by default
            if (manager.hasSystemFeature(PackageManager.FEATURE_TELEVISION)) {
                return false;
            }

            // API 21 uses LEANBACK instead of TELEVISION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (manager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
                    return false;
                }
            }
        }

        // Use small mode on anything smaller than a 7" tablet
        return context.getResources().getConfiguration().smallestScreenWidthDp < 500;
    }

    public static int getDefaultBitrate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return getDefaultBitrate(
                prefs.getString(RESOLUTION_PREF_STRING, DEFAULT_RESOLUTION),
                prefs.getString(FPS_PREF_STRING, DEFAULT_FPS));
    }

    public static int getVideoFormatValue(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String str = prefs.getString(VIDEO_FORMAT_PREF_STRING, DEFAULT_VIDEO_FORMAT);
        if (str.equals("auto")) {
            return AUTOSELECT_H265;
        }
        else if (str.equals("forceh265")) {
            return FORCE_H265_ON;
        }
        else if (str.equals("neverh265")) {
            return FORCE_H265_OFF;
        }
        else {
            // Should never get here
            return AUTOSELECT_H265;
        }
    }

    public void setVideoFormat(String format) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(VIDEO_FORMAT_PREF_STRING, format).apply();
    }

    public static void resetStreamingSettings(Context context) {
        // We consider resolution, FPS, bitrate, HDR, and video format as "streaming settings" here
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .remove(BITRATE_PREF_STRING)
                .remove(BITRATE_PREF_OLD_STRING)
                .remove(LEGACY_RES_FPS_PREF_STRING)
                .remove(RESOLUTION_PREF_STRING)
                .remove(FPS_PREF_STRING)
                .remove(VIDEO_FORMAT_PREF_STRING)
                .remove(ENABLE_HDR_PREF_STRING)
                .remove(UNLOCK_FPS_STRING)
                .apply();
    }

    public static PreferenceConfiguration readPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        PreferenceConfiguration config = new PreferenceConfiguration(context);

        // Migrate legacy preferences to the new locations
        if (prefs.contains(LEGACY_ENABLE_51_SURROUND_PREF_STRING)) {
            if (prefs.getBoolean(LEGACY_ENABLE_51_SURROUND_PREF_STRING, false)) {
                prefs.edit()
                        .remove(LEGACY_ENABLE_51_SURROUND_PREF_STRING)
                        .putString(AUDIO_CONFIG_PREF_STRING, "51")
                        .apply();
            }
        }

        String str = prefs.getString(LEGACY_RES_FPS_PREF_STRING, null);
        if (str != null) {
            if (str.equals("360p30")) {
                config.width = 640;
                config.height = 360;
                config.fps = 30;
            }
            else if (str.equals("360p60")) {
                config.width = 640;
                config.height = 360;
                config.fps = 60;
            }
            else if (str.equals("720p30")) {
                config.width = 1280;
                config.height = 720;
                config.fps = 30;
            }
            else if (str.equals("720p60")) {
                config.width = 1280;
                config.height = 720;
                config.fps = 60;
            }
            else if (str.equals("1080p30")) {
                config.width = 1920;
                config.height = 1080;
                config.fps = 30;
            }
            else if (str.equals("1080p60")) {
                config.width = 1920;
                config.height = 1080;
                config.fps = 60;
            }
            else if (str.equals("4K30")) {
                config.width = 3840;
                config.height = 2160;
                config.fps = 30;
            }
            else if (str.equals("4K60")) {
                config.width = 3840;
                config.height = 2160;
                config.fps = 60;
            }
            else {
                // Should never get here
                config.width = 1280;
                config.height = 720;
                config.fps = 60;
            }

            prefs.edit()
                    .remove(LEGACY_RES_FPS_PREF_STRING)
                    .putString(RESOLUTION_PREF_STRING, getResolutionString(config.width, config.height))
                    .putString(FPS_PREF_STRING, ""+config.fps)
                    .apply();
        }
        else {
            // Use the new preference location
            String resStr = prefs.getString(RESOLUTION_PREF_STRING, PreferenceConfiguration.DEFAULT_RESOLUTION);

            // Convert legacy resolution strings to the new style
            if (!resStr.contains("x")) {
                resStr = PreferenceConfiguration.convertFromLegacyResolutionString(resStr);
                prefs.edit().putString(RESOLUTION_PREF_STRING, resStr).apply();
            }

            config.width = PreferenceConfiguration.getWidthFromResolutionString(resStr);
            config.height = PreferenceConfiguration.getHeightFromResolutionString(resStr);
            config.fps = Integer.parseInt(prefs.getString(FPS_PREF_STRING, PreferenceConfiguration.DEFAULT_FPS));
        }

        if (!prefs.contains(SMALL_ICONS_PREF_STRING)) {
            // We need to write small icon mode's default to disk for the settings page to display
            // the current state of the option properly
            prefs.edit().putBoolean(SMALL_ICONS_PREF_STRING, getDefaultSmallMode(context)).apply();
        }

        // This must happen after the preferences migration to ensure the preferences are populated
        config.bitrate = prefs.getInt(BITRATE_PREF_STRING, prefs.getInt(BITRATE_PREF_OLD_STRING, 0) * 1000);
        if (config.bitrate == 0) {
            config.bitrate = getDefaultBitrate(context);
        }

        String audioConfig = prefs.getString(AUDIO_CONFIG_PREF_STRING, DEFAULT_AUDIO_CONFIG);
        if (audioConfig.equals("71")) {
            config.audioConfiguration = MoonBridge.AUDIO_CONFIGURATION_71_SURROUND;
        }
        else if (audioConfig.equals("51")) {
            config.audioConfiguration = MoonBridge.AUDIO_CONFIGURATION_51_SURROUND;
        }
        else /* if (audioConfig.equals("2")) */ {
            config.audioConfiguration = MoonBridge.AUDIO_CONFIGURATION_STEREO;
        }

        config.videoFormat = getVideoFormatValue(context);

        config.deadzonePercentage = prefs.getInt(DEADZONE_PREF_STRING, DEFAULT_DEADZONE);

        config.oscOpacity = prefs.getInt(OSC_OPACITY_PREF_STRING, DEFAULT_OPACITY);

        config.language = prefs.getString(LANGUAGE_PREF_STRING, DEFAULT_LANGUAGE);

        // Checkbox preferences
        config.disableWarnings = prefs.getBoolean(DISABLE_TOASTS_PREF_STRING, DEFAULT_DISABLE_TOASTS);
        config.enableSops = prefs.getBoolean(SOPS_PREF_STRING, DEFAULT_SOPS);
        config.stretchVideo = prefs.getBoolean(STRETCH_PREF_STRING, DEFAULT_STRETCH);
        config.playHostAudio = prefs.getBoolean(HOST_AUDIO_PREF_STRING, DEFAULT_HOST_AUDIO);
        config.smallIconMode = prefs.getBoolean(SMALL_ICONS_PREF_STRING, getDefaultSmallMode(context));
        config.multiController = prefs.getBoolean(MULTI_CONTROLLER_PREF_STRING, DEFAULT_MULTI_CONTROLLER);
        config.usbDriver = prefs.getBoolean(USB_DRIVER_PREF_SRING, DEFAULT_USB_DRIVER);
        config.onscreenController = prefs.getBoolean(ONSCREEN_CONTROLLER_PREF_STRING, ONSCREEN_CONTROLLER_DEFAULT);
        config.onlyL3R3 = prefs.getBoolean(ONLY_L3_R3_PREF_STRING, ONLY_L3_R3_DEFAULT);
        config.disableFrameDrop = prefs.getBoolean(DISABLE_FRAME_DROP_PREF_STRING, DEFAULT_DISABLE_FRAME_DROP);
        config.enableHdr = prefs.getBoolean(ENABLE_HDR_PREF_STRING, DEFAULT_ENABLE_HDR);
        config.enablePip = prefs.getBoolean(ENABLE_PIP_PREF_STRING, DEFAULT_ENABLE_PIP);
        config.enablePerfOverlay = prefs.getBoolean(ENABLE_PERF_OVERLAY_STRING, DEFAULT_ENABLE_PERF_OVERLAY);
        config.bindAllUsb = prefs.getBoolean(BIND_ALL_USB_STRING, DEFAULT_BIND_ALL_USB);
        config.mouseEmulation = prefs.getBoolean(MOUSE_EMULATION_STRING, DEFAULT_MOUSE_EMULATION);
        config.mouseNavButtons = prefs.getBoolean(MOUSE_NAV_BUTTONS_STRING, DEFAULT_MOUSE_NAV_BUTTONS);
        config.unlockFps = prefs.getBoolean(UNLOCK_FPS_STRING, DEFAULT_UNLOCK_FPS);
        config.vibrateOsc = prefs.getBoolean(VIBRATE_OSC_PREF_STRING, DEFAULT_VIBRATE_OSC);
        config.vibrateFallbackToDevice = prefs.getBoolean(VIBRATE_FALLBACK_PREF_STRING, DEFAULT_VIBRATE_FALLBACK);
        config.flipFaceButtons = prefs.getBoolean(FLIP_FACE_BUTTONS_PREF_STRING, DEFAULT_FLIP_FACE_BUTTONS);
        config.touchscreenTrackpad = prefs.getBoolean(TOUCHSCREEN_TRACKPAD_PREF_STRING, DEFAULT_TOUCHSCREEN_TRACKPAD);
        config.enableLatencyToast = prefs.getBoolean(LATENCY_TOAST_PREF_STRING, DEFAULT_LATENCY_TOAST);

        return config;
    }

}
