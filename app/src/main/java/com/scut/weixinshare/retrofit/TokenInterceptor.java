package com.scut.weixinshare.retrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.scut.weixinshare.MyApplication;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by skyluo on 2018/4/15.
 */

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences preferences = MyApplication.getInstance().getApplicationContext().getSharedPreferences("weixinshare",Context.MODE_PRIVATE);
        String token = preferences.getString("token",null);
        Request originalrequest = chain.request();//原始request
        if(token!=null) {
            Headers headers = new Headers.Builder()
                    .add("Auth-Token",token )
                    .build();//构造一个Headers
            originalrequest = originalrequest.newBuilder().headers(headers).build();//注意这行代码别写错了
        }
        return chain.proceed(originalrequest);
    }
}
