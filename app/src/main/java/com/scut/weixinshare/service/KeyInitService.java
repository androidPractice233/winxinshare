package com.scut.weixinshare.service;

import com.scut.weixinshare.model.ResultBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by skyluo on 2018/4/11.
 */

public interface KeyInitService {

    @POST("/key/keyExchange")
    Call<ResultBean> exchangeKey(@Body String encryptAES);
}
