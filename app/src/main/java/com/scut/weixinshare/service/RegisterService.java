package com.scut.weixinshare.service;

import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterService {
    @POST("/user/register")
    Call<ResultBean> register(@Body User user);
    @POST("/user/login")
    Call<ResultBean> login(@Body User user);
}
