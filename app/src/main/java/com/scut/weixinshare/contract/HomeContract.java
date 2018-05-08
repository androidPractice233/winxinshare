package com.scut.weixinshare.contract;

import com.scut.weixinshare.BasePresenter;
import com.scut.weixinshare.BaseView;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;

import java.io.File;
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

        //设置主页动态尾部为网络错误状态
        void setListErrorView();

        //显示发布动态界面
        void showReleaseMomentUI(Location location);

        //显示动态详情界面（添加评论）
        void showMomentDetailUI(Moment moment, boolean isToComment);

        //显示应用提示信息
        void showReminderMessage(String text);

        //显示动态列表
        void showMomentList();

        //隐藏动态列表
        void hideMomentList();

    }

    interface Presenter extends BasePresenter {

        //向服务器请求新的动态
        void requestNewMoments();

        //向服务器请求后续动态
        void requestNextMoments();

        //前往编辑发布动态
        void toEditReleaseMoment();

        //前往动态详情
        void toMomentDetail(Moment moment);

        //前往发布评论
        void toReleaseComment(Moment moment);

        //发布动态
        void releaseMoment(String text, Location location);

        //发布动态
        void releaseMoment(String text, Location location, List<File> images);

        //设置后续动态请求为可加载状态
        void breakErrorState();

    }
}
