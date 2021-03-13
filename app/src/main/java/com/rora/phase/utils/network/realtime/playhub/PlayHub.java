package com.rora.phase.utils.network.realtime.playhub;

import android.content.Context;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.TransportEnum;
import com.rora.phase.LimeLog;
import com.rora.phase.utils.network.PhaseServiceHelper;

import io.reactivex.Single;

public class PlayHub {

    private HubConnection hubConnection;

    public void startConnect(Context context, PlayHubListener listener) {
        //String token = UserRepository.newInstance(context).getUserToken();
        String token = "s";
        hubConnection = HubConnectionBuilder.create(PhaseServiceHelper.playHubUrl).withTransport(TransportEnum.LONG_POLLING)
                //.withHeader("Authorization", token)
                .withAccessTokenProvider(Single.defer(() -> Single.just(token)))
                .build();

        hubConnection.onClosed(exception -> {
            if (listener != null)
                listener.onDisconnected();
        });

        hubConnection.start().subscribe(() -> {
            LimeLog.info("Connect success");
            listener.onConnected();
        }, throwable -> {
            LimeLog.info("Connect hub err: - " + throwable.getMessage());
             listener.onDisconnected();
        });
    }

    public void stopConnect() {
        hubConnection.stop();
    }

}