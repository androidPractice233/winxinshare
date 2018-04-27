package com.scut.weixinshare.contract;

import android.os.Bundle;

import com.scut.weixinshare.BasePresenter;
import com.scut.weixinshare.BaseView;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Moment;

public interface MomentDetailContract {

    interface View extends BaseView<Presenter>{

        //设置界面显示信息
        void setView(Moment moment);

        //显示软键盘
        void showKeyBroad();

        //向界面中添加评论
        void addComment(Comment comment);

        //显示刷新提示
        void showRefreshing();

        //隐藏刷新提示
        void hideRefreshing();

        //显示应用提示信息
        void showReminderMessage(String message);

    }

    interface Presenter extends BasePresenter{

        //发布评论
        void releaseComment(String text, String receiverId);

        //返回给主页的结果
        Bundle resultToHome();

        //获取动态正文
        void requestMomentDetail();

    }

}
