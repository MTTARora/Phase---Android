package com.rora.phase.utils.network;

import com.rora.phase.utils.DataResult;

public class BaseResponse<T> {

    private String status;
    private int statusCode;
    private String message;
    private T data;

    public static <T> DataResult<T> getResult(BaseResponse<T> response) {
        DataResult<T> resp = new DataResult<>();

        if(response != null && response.statusCode == 200) {
            resp.setData(response.data);
        }

        resp.setMsg(response.message);

        return (resp.getData() == null && resp.getMsg() == null) ? null : resp;
    }

}
