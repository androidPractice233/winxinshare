package com.scut.weixinshare.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.contract.UserContract;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.LoginReceive;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.model.source.MomentUserData;
import com.scut.weixinshare.retrofit.BaseCallback;
import com.scut.weixinshare.utils.GlideUtils;
import com.scut.weixinshare.utils.MomentUtils;
import com.scut.weixinshare.view.LoginActivity;
import com.scut.weixinshare.view.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.scut.weixinshare.MyApplication.getContext;

public class UserPresenter implements UserContract.Presenter {

    private UserContract.View view;
    private User user;

    public User getUser() {
        return user;
    }

    public UserPresenter(UserContract.View view, User user){
        this.view = view;
        this.user =user;
        view.setPresenter(this);
    }
    public UserPresenter(UserContract.View view, String userid){
        this.view=view;
        setShowUser(userid);
        view.setPresenter(this);
    }
    @Override
    public void start() {
        view.showUserInfo(user);
    }

    @Override
    public void setShowUser(String userId) {
        if(!userId.equals(MyApplication.currentUser.getUserId())) {
            NetworkManager.getInstance().getUser(new Callback<ResultBean>() {
                @Override
                public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                    ResultBean resultBean=response.body();
                    Log.d(TAG, "onResponse: "+resultBean.toString());
                }

                @Override
                public void onFailure(Call<ResultBean> call, Throwable t) {
                    Log.e(TAG, "无此userId");
                }
            },userId);
        }
        else
            user=MyApplication.currentUser;

    }

    @Override
    public void updateUserInfo() {
        NetworkManager.getInstance().updateUserInfo(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean=response.body();
                Log.d(TAG, "onResponse: "+resultBean.toString());
                if (resultBean.getCode()==200)
                    Log.d(TAG, "onResponse: 修改用户信息成功");
                else
                    Log.d(TAG, "onResponse: 修改失败");
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                Log.d(TAG, "网络通信失败");

            }
        },MyApplication.currentUser);

    }

    @Override
    public void getUserPhoto(User user) {

    }


    @Override
    public void updateUserPhoto(List<File> fileList) {
        try {
            NetworkManager.getInstance().MutiprtTest(new Callback<ResultBean>() {
                @Override
                public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                    ResultBean resultBean=  response.body();
                }

                @Override
                public void onFailure(Call<ResultBean> call, Throwable t) {
                    Log.e("updateUserPhoto", t.getMessage()  );
                }
            },fileList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
