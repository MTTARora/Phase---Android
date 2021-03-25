package com.rora.phase.utils;

public class DataResultHelper<T> {
    private String errMsg;
    private T data;

    public DataResultHelper() {
    }

    public DataResultHelper(String errMsg, T data) {
        this.errMsg = errMsg;
        this.data = data;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
