package com.scut.weixinshare.service;

import android.net.Uri;

import com.scut.weixinshare.model.ResultBean;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by skyluo on 2018/4/14.
 * ！！！！
 * ！！！！
 * 注意：所有multipart/form-data类型的请求请写在这里！！！！
 * ！！！！
 * ！！！！
 *
 */

public interface MultipartService {
    @Multipart
    @POST("MultipartTest")
    Call<ResultBean> test(@Part("test") String test1, @Part List<MultipartBody.Part> file);

    @Multipart
    @POST("/moment/piccontent")
    Call<ResultBean> uploadMomentImages(@Part("momentId") String momentId,
                                                   @Part List<MultipartBody.Part> file);
    @Multipart
    @POST("/user/portrait")
    Call<ResultBean> uploadUserPortrait(@Part("userId") String userId,
                                        @Part MultipartBody.Part file);



}
