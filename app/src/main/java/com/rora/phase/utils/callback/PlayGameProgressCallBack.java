package com.rora.phase.utils.callback;

import com.rora.phase.nvstream.http.ComputerDetails;

public interface PlayGameProgressCallBack {
    void onStart(boolean isDone);
    void onFindAHost(boolean isDone);
    void onAddPc(boolean isDone);
    void onPairPc(boolean isDone);
    void onStartConnect(boolean isDone);
    void onComputerUpdated(ComputerDetails computer);
    void onStopConnect(boolean isDone);
    void onError(String err);
}