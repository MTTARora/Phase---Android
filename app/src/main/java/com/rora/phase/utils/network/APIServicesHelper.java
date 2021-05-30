package com.rora.phase.utils.network;

import com.rora.phase.RoraLog;
import com.rora.phase.utils.DataResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIServicesHelper<T> {

    public APIServicesHelper() {
    }

    public void request (Call<BaseResponse<T>> request, OnResponseCallback<T> callBack) {
        RoraLog.info("API Services - request to " + request.request().url());

        request.enqueue(new Callback<BaseResponse<T>>() {
            @Override
            public void onResponse(Call<BaseResponse<T>> call, Response<BaseResponse<T>> response) {
                DataResponse<BaseResponse<T>> dataResponse = PhaseServiceHelper.handleResponse(response);
                String err = dataResponse.getMsg();
                T data = null;

                if (err == null) {
                    DataResponse<T> dataResp = BaseResponse.getResult(dataResponse.getData());
                    if (dataResp == null)
                        err = "No data from server";
                    else {
                        if (dataResp.getData() != null)
                            data = dataResp.getData();
                        else
                            err = dataResp.getMsg();
                    }
                }

                if (err != null && !err.contains("success"))
                    RoraLog.warning("API Services - request err: " + err);

                if (err != null && err.contains("success"))
                    err = null;

                callBack.onResponse(err, data);
            }

            @Override
            public void onFailure(Call<BaseResponse<T>> call, Throwable t) {
                RoraLog.warning("API Services - Can't connect to server: " + request.request().url() + " - " + t.getMessage());
                callBack.onResponse("Could not connect to server, please try again later!", null);
            }
        });
    }

    public interface OnResponseCallback<T> {
        void onResponse(String err, T data);
    }

}
