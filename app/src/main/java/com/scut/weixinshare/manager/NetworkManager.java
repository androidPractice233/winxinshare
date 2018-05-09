package com.scut.weixinshare.manager;

import android.util.Log;


import com.google.gson.Gson;
import com.scut.weixinshare.IConst;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.retrofit.EncryptConverterFactory;
import com.scut.weixinshare.retrofit.TokenInterceptor;
import com.scut.weixinshare.service.KeyInitService;
import com.scut.weixinshare.service.MultipartService;
import com.scut.weixinshare.service.PullCommentService;
import com.scut.weixinshare.service.TestService;
import com.scut.weixinshare.utils.AES;
import com.scut.weixinshare.utils.NetworkUtils;
import com.scut.weixinshare.utils.RSA;
import com.scut.weixinshare.view.MainActivity;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;


import java.io.File;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;


/**
 * Created by skyluo on 2018/4/11.
 */

public class NetworkManager {
    private static NetworkManager networkManager;
    private   Retrofit retrofit;
    private   Retrofit multipartRetrofit;
    private  String AESkey;
    private OkHttpClient okHttpClient;
    public String getAESkey() {
        return AESkey;
    }
   // 懒汉式，线程安全
    public static synchronized  NetworkManager getInstance() {
     if(networkManager==null) {
         networkManager = new NetworkManager();
         networkManager.init();
     }
     return networkManager;

}

    public    void exchangeKey() throws Exception {
        this.AESkey=AES.generateKeyString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IConst.URL_BASE)
                .client(genericClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();//增加返回值为实体类的支持
        KeyInitService keyInitService=retrofit.create(KeyInitService.class);
        //加载RSA公钥
        RSAPublicKey rsaPublicKey = RSA.loadPublicKey(MyApplication.getInstance().getAssets().open("rsa_public_key.pem"));
        Log.i("RSA 加密前的AES",  AESkey);
        String encryptData=RSA.encryptByPublicKey(AESkey,rsaPublicKey);
        Log.i("RSA 加密后的AES",  encryptData);
        Call<ResultBean> call=keyInitService.exchangeKey(encryptData);
        call.enqueue(new Callback<ResultBean>(){

            @Override
            public void onResponse(Call<ResultBean> call, retrofit2.Response<ResultBean> response) {
                Log.i("交换aes", "onResponse:交换aes key成功");
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                Log.e("交换aes", "onResponse:交换aes key失败，程序不能进行");

            }
        });


    }
    public OkHttpClient genericClient() {
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(MyApplication.getInstance().getApplicationContext()));

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new TokenInterceptor())
                .cookieJar(cookieJar)
                .build();
        return httpClient;
    }

    public  void init(){
        this.okHttpClient=genericClient();
        retrofit = new Retrofit.Builder()
                .baseUrl(IConst.URL_BASE)
                .client(okHttpClient)
                .addConverterFactory(EncryptConverterFactory.create())
                .build();//增加返回值为实体类的支持
        //专门进行multipart请求的retrofit,不进行加解密！
        multipartRetrofit=new Retrofit.Builder()
                .baseUrl(IConst.URL_BASE)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();//增加返回值为实体类的支持
        return;

    }
    public  void test(Callback<ResultBean> callback){
//        TestService testService= retrofit.create(TestService.class);
//        Call<ResultBean> call=testService.test("我不想写代码！");
//        call.enqueue(callback);
        TestService testService= retrofit.create(TestService.class);
        Map<String, Object> params = new HashMap<>();
        params.put("userName", "devin");
        //params.put("nickName", "devin");
        params.put("userPwd", "123");
        //params.put("location", "beijing");
        //params.put("sex", "1");
        //params.put("birthday", "20011010");
        Call<ResultBean> call=testService.login(params);
        call.enqueue(callback);
    }

    public void pullComment(Callback<ResultBean> callback,String time){
        PullCommentService pullCommentService = retrofit.create(PullCommentService.class);

        Map<String, Object> params = new HashMap<>();
        //params.put("token", MainActivity.TOKEN);
        params.put("dateTime",1);
        params.put("pageNum",0);
        params.put("pageSize",20);
        Call<ResultBean> call=pullCommentService.pullComment(params);
        call.enqueue(callback);
    }




    public  void MutiprtTest(Callback<ResultBean> callback, List<File> fileList) throws IOException {
        MultipartService multipartService=multipartRetrofit.create(MultipartService.class);
        Call<ResultBean> call=multipartService.test("卧槽", NetworkUtils.filesToMultipartBodyParts(fileList,"fileList"));
        call.enqueue(callback);
    }
}
