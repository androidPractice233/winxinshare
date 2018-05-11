package com.scut.weixinshare.contract;

import com.scut.weixinshare.BasePresenter;
import com.scut.weixinshare.BaseView;
import com.scut.weixinshare.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface UserContract {
    interface View extends BaseView<Presenter> {
        //显示个人信息
        void showUserInfo(User user);

        //显示头像
        void showUserPhoto(User user) throws FileNotFoundException;

    }
    interface Presenter extends BasePresenter {
        //获取个人信息
        void setShowUser(String userid);


        void updateUserInfo();

        //获取头像
        void getUserPhoto(User user);

        //上传新头像
        void updateUserPhoto(List<File> fileList);


        User getUser();

    }

}
