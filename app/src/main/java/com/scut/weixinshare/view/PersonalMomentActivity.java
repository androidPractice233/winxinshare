package com.scut.weixinshare.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.luck.picture.lib.config.PictureConfig;
import com.scut.weixinshare.R;
import com.scut.weixinshare.model.source.LocationRepository;
import com.scut.weixinshare.model.source.MomentsRepository;
import com.scut.weixinshare.model.source.local.MomentDatabaseSource;
import com.scut.weixinshare.model.source.remote.MomentRemoteServerSource;
import com.scut.weixinshare.presenter.HomePresenter;
import com.scut.weixinshare.presenter.PersonHomePresenter;
import com.scut.weixinshare.view.fragment.HomeFragment;
import com.scut.weixinshare.view.fragment.PersonHomeFragment;

/**
 * Created by skyluo on 2018/5/8.
 */

public class PersonalMomentActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String userid = intent.getStringExtra("userId");
            PersonHomeFragment fragment = new PersonHomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_home, fragment);
            transaction.commit();
            new PersonHomePresenter(fragment, MomentsRepository.getInstance(MomentDatabaseSource.getInstance(), MomentRemoteServerSource.getInstance()),
                    LocationRepository.getInstance(), userid);
    }
    public static void actionStart(android.content.Context context) {
        Intent intent = new Intent(context, PersonalMomentActivity.class);
        context.startActivity(intent);
    }

    }

