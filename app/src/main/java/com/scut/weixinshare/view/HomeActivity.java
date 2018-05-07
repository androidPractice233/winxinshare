package com.scut.weixinshare.view;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.luck.picture.lib.config.PictureConfig;
import com.scut.weixinshare.R;
import com.scut.weixinshare.model.source.LocationRepository;
import com.scut.weixinshare.model.source.MomentsRepository;
import com.scut.weixinshare.presenter.HomePresenter;
import com.scut.weixinshare.view.fragment.HomeFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(savedInstanceState == null) {
            HomeFragment fragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_home, fragment, PictureConfig.FC_TAG);
            transaction.commit();
            new HomePresenter(fragment, MomentsRepository.getInstance(),
                    LocationRepository.getInstance());
        } else {
            HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                    .findFragmentByTag(PictureConfig.FC_TAG);
            new HomePresenter(fragment, MomentsRepository.getInstance(),
                    LocationRepository.getInstance());
        }
    }
}
