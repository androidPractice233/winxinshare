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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
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
        userid=intent.getStringExtra("userId");
        fragment=UserFragment.newInstance(userid);
        android.support.v4.app.FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_user,fragment,"user");
        transaction.commit();
    }






    public interface showTitle{
        public void changeTitle(String title);
    }
}
