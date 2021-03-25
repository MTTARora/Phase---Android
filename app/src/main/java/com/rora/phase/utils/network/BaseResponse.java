package com.rora.phase.utils.network;

import com.rora.phase.model.Game;

import java.util.List;

public class BaseResponse<T> {

    private String status;
    private int statusCode;
    private String message;
    private T data;

    public static <T> T getResult(BaseResponse<T> response) {
        //Optimize here
        if(response != null && response.statusCode == 200) {
            return response.data;
        }
        return null;
    }

}
