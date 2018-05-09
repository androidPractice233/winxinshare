package com.scut.weixinshare.presenter;

import android.util.Log;

import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.contract.UserContract;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

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
    @Override
    public void start() {
        view.showUserInfo(user);
    }

    @Override
    public void getUserInfo(String userId) {
        if(!userId.equals(MyApplication.user.getUserId())) {
            NetworkManager.getInstance().getUser(new Callback<ResultBean>() {
                @Override
                public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                    ResultBean resultBean = response.body();
                    user = (User) resultBean.getData();
                }

                @Override
                public void onFailure(Call<ResultBean> call, Throwable t) {
                    Log.d(TAG, "onFailure: invalid userId");
                }
            }, userId);
        }
        else
            user=MyApplication.user;

    }

    @Override
    public void updateUserInfo() {
        NetworkManager.getInstance().updateUserInfo(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean=response.body();
                if (resultBean.getCode()==200)
                    Log.d(TAG, "onResponse: 修改用户信息成功");
                else
                    Log.d(TAG, "onResponse: 修改失败");
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                Log.d(TAG, "网络通信失败");

            }
        },user);

    }

    @Override
    public void getUserPhoto() {

    }

    @Override
    public void updateUserPhoto() {

    }

    @Override
    public void toUserName() {

    }
}
