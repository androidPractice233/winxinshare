package com.scut.weixinshare.retrofit;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.scut.weixinshare.Exception.CryptException;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.EncryptBean;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.utils.AES;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static android.R.attr.value;

/**
 * Created by skyluo on 2018/4/11.
 */

public class EncryptResponseBodyConverter <T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;
    public EncryptResponseBodyConverter(Gson gson, Type type) {

        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        String result=responseBody.string();
      EncryptBean encryptBean=gson.fromJson(result,EncryptBean.class);
       if(!encryptBean.isSuccess()){
           //服务器加密错误,可能是服务器session过期导致，获取aes key    失败
           //尝试进行AES交换
           try {
               NetworkManager.getInstance().exchangeKey();
           } catch (Exception e) {
               e.printStackTrace();
           }
           throw new CryptException("服务器Session中没有AES KEY");
       }
       else {
           String encryptData=encryptBean.getEncyptData();
           String Data=AES.decrypt(encryptData, NetworkManager.getInstance().getAESkey());
          return gson.fromJson(Data,type);

       }
    }
}
