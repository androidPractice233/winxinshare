package com.scut.weixinshare;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by mac on 2018/4/9.
 */

public interface LoginService {
    /**接口名字：aa
     * @FormUrlEncoded post请求需要添加的编码
     * @POST("LoginServlet") ：post请求和请求接口(url后一个/后的字符串地址的最)
     * @Field("username")：post请求的参数名称(服务器接口参数名称)，会自动添加到参数集合
     * String uname:本地的参数名称，unname是自己定义的
     *返回值： Call<T>,T是返回的类型
     */

    //登录
    @FormUrlEncoded
    @POST("LoginServlet")
    Call<String> login(@Field("username") String username,
                       @Field("pwd") String password);

    @FormUrlEncoded
    @POST("LoginServlet")
    public Call<String> login(@FieldMap Map<String, String> pramsMap);


}
