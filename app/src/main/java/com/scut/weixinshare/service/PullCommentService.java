package com.scut.weixinshare.service;

import com.scut.weixinshare.model.ResultBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PullCommentService {
    @POST("comment/pull")
    Call<ResultBean> pullComment(@Body Map praram);
}
