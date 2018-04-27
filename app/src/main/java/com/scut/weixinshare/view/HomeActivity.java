package com.scut.weixinshare.view;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.luck.picture.lib.config.PictureConfig;
import com.scut.weixinshare.R;
import com.scut.weixinshare.view.fragment.HomeActivityFragment;
import com.scut.weixinshare.presenter.HomePresenter;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(savedInstanceState == null) {
            HomeActivityFragment fragment = new HomeActivityFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_home, fragment, PictureConfig.FC_TAG);
            transaction.commit();
            new HomePresenter(fragment);
        } else {
            HomeActivityFragment fragment = (HomeActivityFragment)getSupportFragmentManager()
                    .findFragmentByTag(PictureConfig.FC_TAG);
            new HomePresenter(fragment);
        }
    }
}
