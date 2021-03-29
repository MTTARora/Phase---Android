package com.rora.phase.utils;

public class DataResponse<T> {
    private String msg;
    private T data;

    public DataResponse() {
    }

    public DataResponse(String msg, T data) {
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
