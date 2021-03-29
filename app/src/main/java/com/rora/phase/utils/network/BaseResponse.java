package com.rora.phase.utils.network;

import com.rora.phase.model.Game;
import com.rora.phase.utils.DataResponse;

import java.util.List;

public class BaseResponse<T> {

    private String status;
    private int statusCode;
    private String message;
    private T data;

    public static <T> DataResponse<T> getResult(BaseResponse<T> response) {
        DataResponse<T> resp = new DataResponse<>();

        if(response != null && response.statusCode == 200) {
            resp.setData(response.data);
        }

        resp.setMsg(response.message);

        return (resp.getData() == null && resp.getMsg() == null) ? null : resp;
    }

}
