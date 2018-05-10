package com.scut.weixinshare.contract;

import android.content.Intent;
import android.net.Uri;

import com.scut.weixinshare.BasePresenter;
import com.scut.weixinshare.BaseView;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Moment;

import java.util.ArrayList;
import java.util.List;

public interface MomentDetailContract {

    interface View extends BaseView<Presenter>{

        //设置界面显示信息
        void setView(Moment moment);

        //显示软键盘
        void showKeyBroad();

        //显示刷新提示
        void showRefreshing();

        //隐藏刷新提示
        void hideRefreshing();

        //显示应用提示信息
        void showReminderMessage(String message);

        //显示用户信息界面
        void showUserDataUI(String userId);

        //更新评论的提示信息
        void updateCommentUI(String nickname);

        //显示登录界面
        void showLoginUI();

        //显示大图界面
        void showBigPicUI(ArrayList<Uri> images);

    }

    interface Presenter extends BasePresenter{

        //发布评论
        void releaseComment(String text);

        //返回给主页的结果
        Intent returnData();

        //获取动态正文
        void refreshMomentDetail();

        //打开用户信息
        void openUserData(Moment moment);

        //改变评论对象
        void changeCommentUser(Comment comment);

        //打开大图
        void openBigImage(List<Uri> uriList);

    }

}
