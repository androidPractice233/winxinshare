package com.scut.weixinshare.contract;

import android.os.Bundle;

import com.scut.weixinshare.BasePresenter;
import com.scut.weixinshare.BaseView;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;

import java.util.List;

//动态主页交互接口
public interface HomeContract {

    interface View extends BaseView<Presenter> {

        //初始化主页动态
        void initMoments(List<Moment> momentList);

        //尾部追加主页动态
        void addMoments(List<Moment> momentList);

        //显示刷新提示
        void showRefreshing();

        //隐藏刷新提示
        void hideRefreshing();

        //设置主页动态尾部为没有更多内容状态
        void setListEndView();

        //设置主页动态尾部为加载状态
        void setListLoadingView();

        //显示发布动态界面
        void showReleaseMomentUI(Location location);

        //显示动态详情界面（添加评论）
        void showMomentDetailUI(Moment moment, boolean isToComment);

        //显示应用提示信息
        void showReminderMessage(String text);

    }

    interface Presenter extends BasePresenter {

        //获取用户当前位置
        void getLocation();

        //向服务器请求新的动态
        void requestNewMoments();

        //向服务器请求后续动态
        void requestNextMoments();

        //前往发布动态
        void toReleaseMoment();

        //前往动态详情
        void toMomentDetail(Moment moment);

        //前往发布评论
        void toReleaseComment(Moment moment);

    }
}
