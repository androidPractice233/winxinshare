package com.scut.weixinshare;

import android.app.Application;
import android.content.Context;

import com.scut.weixinshare.model.User;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.retrofit.EncryptConverterFactory;
import com.scut.weixinshare.service.KeyInitService;
import com.scut.weixinshare.utils.AES;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.UserDictionary.Words.APP_ID;

/**
 * 存放全局变量
 *
 * @author sszvip
 *
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication instance;
    private static Context context;
    private static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
        try {
            NetworkManager.getInstance().exchangeKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MyApplication getInstance(){
        return instance;
    }

    public static Context getContext() {
        return context;
    }
}
