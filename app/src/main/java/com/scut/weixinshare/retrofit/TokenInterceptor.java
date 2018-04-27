package com.scut.weixinshare.retrofit;

import android.content.Context;
import android.content.SharedPreferences;

import com.scut.weixinshare.MyApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by skyluo on 2018/4/15.
 */

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences preferences = MyApplication.getInstance().getApplicationContext().getSharedPreferences("weixinshare",Context.MODE_PRIVATE);
        String token = preferences.getString("token",null);

        Response originalResponse = chain.proceed(chain.request());
        if(token!=null) {
            return originalResponse.newBuilder()
                    .header("token", token)
                    .build();
        }
        else return originalResponse;
    }
}
