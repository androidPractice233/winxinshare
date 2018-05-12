package com.scut.weixinshare.contract;

import android.graphics.Bitmap;
import android.net.Uri;

import com.scut.weixinshare.BasePresenter;
import com.scut.weixinshare.BaseView;
import com.scut.weixinshare.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface UserContract {
    interface View extends BaseView<Presenter> {
        //显示个人信息
        void showUserInfo(User user);

        //显示头像
        void setPortrait(File portrait);
        //设置头像byuri
        void setPortrait(String  uri);
    }
    interface Presenter extends BasePresenter {

        //设置要显示个人主页的用户（登陆者或从动态跳转）
        void setShowUser(String userid);

        //更新个人信息
        void updateUserInfo();

        //更新头像
        void updatePortrait(File portrait) throws IOException;

       //返回presenter持有的user对象
        User getUser();

    }

}
