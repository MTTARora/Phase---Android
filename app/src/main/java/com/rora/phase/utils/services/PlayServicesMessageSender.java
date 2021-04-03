package com.rora.phase.utils.services;

public class PlayServicesMessageSender {
    public enum MsgCode {
        PLAY,
        STOP,
    }

    public interface Sender {
        void sendMessage(MsgCode code);
    }
}
