package com.scut.weixinshare.service;

import com.scut.weixinshare.model.ResultBean;

import java.util.Date;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetNearbyMomentService {
    @POST("/moment/nearby")
    Call<ResultBean> getNearbyMoment(@Body String token, double longitude, double latitude,
                                     int pageNum, int pageSize);
}
