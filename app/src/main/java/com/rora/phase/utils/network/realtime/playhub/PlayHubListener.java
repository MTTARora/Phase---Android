package com.rora.phase.utils.network.realtime.playhub;

import com.rora.phase.model.Host;

public interface PlayHubListener {
    void onConnected();
    void onUpdatePlayQueue(int position);
    void onHostAvailable(Host host); //In case user is in queue then have available host
    void onAppReady(boolean isSuccess);
    void onDisconnected(int code);
}
