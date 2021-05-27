package com.rora.phase.utils.callback;

public interface PlayGameProgressCallBack {
    void onStart(boolean isDone);
    void onConnectionEstablished(boolean isSuccess);
    void onFindAHost(boolean isDone);
    void onQueueUpdated(boolean isFirstInit, int total, int position);
    void onPairPc(boolean isDone);
    void onGetHostApps(boolean isDone);
    void onPrepareHost(boolean isDone);
    void onStartConnect(boolean isDone);
    void onPaused(String err);
    void onResumed(String err);
    void onStopConnect(boolean isDone, String err);
    void onError(String err);
}
