package com.rora.phase.binding.input.evdev;


import android.app.Activity;

import com.rora.phase.LimelightBuildProps;
import com.rora.phase.binding.input.capture.InputCaptureProvider;

public class EvdevCaptureProviderShim {
    public static boolean isCaptureProviderSupported() {
        return LimelightBuildProps.ROOT_BUILD;
    }

    // We need to construct our capture provider using reflection because it isn't included in non-root builds
    public static InputCaptureProvider createEvdevCaptureProvider(Activity activity, EvdevListener listener) {
        try {
            Class providerClass = Class.forName("com.rora.phase.binding.input.evdev.EvdevCaptureProvider");
            return (InputCaptureProvider) providerClass.getConstructors()[0].newInstance(activity, listener);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
