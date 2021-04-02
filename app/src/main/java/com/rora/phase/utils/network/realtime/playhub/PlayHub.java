package com.rora.phase.utils.network.realtime.playhub;

import android.content.Context;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.microsoft.signalr.TransportEnum;
import com.rora.phase.RoraLog;
import com.rora.phase.repository.UserRepository;
import com.rora.phase.utils.network.PhaseServiceHelper;

import io.reactivex.Single;

public class PlayHub {

    private HubConnection hubConnection;

    public void startConnect(Context context, PlayHubListener listener) {
        String token = UserRepository.newInstance(context).getUserToken();
        hubConnection = HubConnectionBuilder.create(PhaseServiceHelper.playHubUrl).withTransport(TransportEnum.WEBSOCKETS)
                //.withHeader("Authorization", token)
                .withAccessTokenProvider(Single.defer(() -> Single.just(token)))
                .build();

        hubConnection.onClosed(exception -> {
            if (listener != null)
                listener.onDisconnected(200);
        });

        hubConnection.start().subscribe(() -> {
            RoraLog.info("Connect hub success");
            listener.onConnected();
        }, throwable -> {
            if (throwable.getMessage().contains("401")) {
                RoraLog.info("Connect hub err: - Your login session has ended, please login again!");
                listener.onDisconnected(401);
            } else {
                RoraLog.info("Connect hub err: - " + throwable.getMessage());
                listener.onDisconnected(400);
            }
        });
    }

    public void stopConnect() {
        if (!(hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED))
            hubConnection.stop();
    }

}
