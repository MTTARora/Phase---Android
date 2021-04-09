package com.rora.phase.utils.callback;

public interface PlayGameProgressCallBack {
    void onStart(boolean isDone);
    void onFindAHost(boolean isDone);
    void onJoinQueue(int total, int position);
    void onPairPc(boolean isDone);
    void onGetHostApps(boolean isDone);
    void onPrepareHost(boolean isDone);
    void onStartConnect(boolean isDone);
    void onStopConnect(boolean isDone, String err);
    void onError(String err);
}
