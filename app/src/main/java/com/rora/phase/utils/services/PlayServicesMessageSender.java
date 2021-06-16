package com.rora.phase.utils.services;

public class PlayServicesMessageSender {
    public enum MsgCode {
        PLAY,
        SWITCH,
        RESUME,
        STOP,
    }

    public interface Sender {
        void sendMessage(MsgCode code);
    }
}
