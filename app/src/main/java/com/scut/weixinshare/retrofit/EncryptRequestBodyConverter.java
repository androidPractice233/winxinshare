package com.scut.weixinshare.retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.APIBodyData;
import com.scut.weixinshare.utils.AES;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created by skyluo on 2018/4/11.
 */

public class EncryptRequestBodyConverter <T> implements Converter<T, RequestBody> {
    private final Gson gson;
    private final Type type;
    public EncryptRequestBodyConverter(Gson gson, Type type) {

        this.gson = gson;
        this.type = type;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        gson.toJson(value,type);
        Log.i("xiaozhang", "request中传递的json数据：" + value.toString());
        String AesKey= NetworkManager.getInstance().getAESkey();
        String encryptData= AES.encrypt(value.toString(),AesKey);
        Log.i("xiaozhang", "转化后的数据：" + encryptData);
        return RequestBody.create(MediaType.parse("application/json"), encryptData);
    }
}
