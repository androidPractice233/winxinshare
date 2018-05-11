package com.scut.weixinshare.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.presenter.UserPresenter;
import com.scut.weixinshare.view.fragment.UserFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    Toolbar toolbar;
    User currentUser;
    UserFragment fragment;
    String userid;
    UserPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent=getIntent();
        userid=intent.getStringExtra("userId");
        setTitle("个人页面");
        fragment=UserFragment.newInstance(userid);
        presenter=new UserPresenter(fragment,userid);

        android.support.v4.app.FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_user,fragment,"user");
        transaction.commit();
    }

    public static void actionStart(android.content.Context context,String userid) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra("userId",userid);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<File> fileList = new ArrayList<>();
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                for (LocalMedia p : selectList) {
                    fileList.add(new File(p.getPath()));
                }

                if (fileList.size() > 0) {
                    try {
                        presenter.updatePortrait(fileList.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public interface showTitle{
        public void changeTitle(String title);
    }
}
