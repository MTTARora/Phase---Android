package com.rora.phase.binding;

import android.content.Context;

import com.rora.phase.binding.audio.AndroidAudioRenderer;
import com.rora.phase.binding.crypto.AndroidCryptoProvider;
import com.rora.phase.nvstream.av.audio.AudioRenderer;
import com.rora.phase.nvstream.http.LimelightCryptoProvider;

public class PlatformBinding {
    public static String getDeviceName() {
        String deviceName = android.os.Build.MODEL;
        deviceName = deviceName.replace(" ", "");
        return deviceName;
    }

    public static AudioRenderer getAudioRenderer() {
        return new AndroidAudioRenderer();
    }

    public static LimelightCryptoProvider getCryptoProvider(Context c) {
        return new AndroidCryptoProvider(c);
    }
}
