package com.scut.weixinshare.service;

import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.source.MomentVersion;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by skyluo on 2018/4/11.
 */

public interface TestService {

    @POST("test")
    Call<ResultBean> test(@Body String test);

    @POST("user/register")
    Call<ResultBean> register(@Body Map praram);

    @POST("/moment/nearby")
    Call<ResultBean<List<MomentVersion>>> requestNearbyMoment(@Body Map params);

    @POST("/moment/detail")
    Call<ResultBean> requestMomentDetail(@Body Map params);

    @POST("/moment/create")
    Call<ResultBean> createMoment(@Body Map params);

    @POST("/comment/create")
    Call<ResultBean> createComment(@Body Map params);

    @POST("/user/getnickpot")
    Call<ResultBean> requestNicknameAndPortrait(@Body Map params);
    @POST("/moment/personal")
    Call<ResultBean<List<MomentVersion>>> requestPersonMoment(@Body Map params);
}
