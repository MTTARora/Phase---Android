package com.rora.phase.utils.callback;

public interface OnResultCallBack<T> {
    void onResult(String errMsg, T data);
}
