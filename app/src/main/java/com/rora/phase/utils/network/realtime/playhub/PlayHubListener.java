package com.rora.phase.utils.network.realtime.playhub;

public interface PlayHubListener {
    void onConnected();
    void onAppReady();
    void onDisconnected(int code);
}
