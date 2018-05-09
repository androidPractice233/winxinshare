package com.scut.weixinshare.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.presenter.UserPresenter;
import com.scut.weixinshare.view.fragment.UserFragment;
import com.scut.weixinshare.view.fragment.UserInputFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity implements UserInputFragment.FragmentInteraction {
    Toolbar toolbar;
    User currentUser;
    UserFragment fragment;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        userid=intent.getStringExtra("userid");

        if(savedInstanceState==null){
            fragment=UserFragment.newInstance(currentUser);
            android.support.v4.app.FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_user,fragment,"user");
            transaction.commit();
            new UserPresenter(fragment,currentUser);
        }else{
            fragment=(UserFragment)getSupportFragmentManager()
                    .findFragmentByTag("user");
            new UserPresenter(fragment,currentUser);
        }

    }

    @Override
    public void changeTitle(String title) {
        setTitle(title);
    }

    @Override
    public void updateUser(User user) {
        fragment.showUserInfo(user);
    }

    public void getCurrentUser() {
        if(userid.isEmpty())
            userid= MyApplication.getCurrentUser().getUserId();
        NetworkManager.getInstance().getUser(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean=response.body();
                currentUser=(User)resultBean.getData();
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {

            }
        },userid);
    }


    public interface showTitle{
        public void changeTitle(String title);
    }
}
