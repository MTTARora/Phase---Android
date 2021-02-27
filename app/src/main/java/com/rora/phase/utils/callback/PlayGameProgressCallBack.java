package com.rora.phase.utils.callback;

public interface PlayGameProgressCallBack {
    void onAddPc(boolean isDone);
    void onPairPc(boolean isDone);
    void onStartConnect(boolean isDone);
    void onStopConnect(boolean isDone);
    void onError(String err);
}
