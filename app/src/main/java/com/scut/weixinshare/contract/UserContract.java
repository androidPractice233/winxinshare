package com.scut.weixinshare.contract;

import com.scut.weixinshare.BasePresenter;
import com.scut.weixinshare.BaseView;
import com.scut.weixinshare.model.User;

import java.io.File;
import java.util.List;

public interface UserContract {
    interface View extends BaseView<Presenter> {
        //显示个人信息
        void showUserInfo(User user);

        //显示头像
        void showUserPhoto();

    }
    interface Presenter extends BasePresenter {
        //获取个人信息
        void setShowUser(String userid);


        void updateUserInfo();

        //获取头像
        void getUserPhoto();

        //上传新头像
        void updateUserPhoto(List<File> fileList);


        User getUser();

    }

}
