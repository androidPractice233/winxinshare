package com.scut.weixinshare.presenter;

import com.scut.weixinshare.contract.HomeContract;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.LocationDataSource;
import com.scut.weixinshare.model.source.MomentDataSource;
import com.scut.weixinshare.utils.NetworkUtils;

import java.util.List;

/**
 * Created by skyluo on 2018/5/8.
 */

public class PersonHomePresenter extends HomePresenter {
    private String userid;

    public PersonHomePresenter(HomeContract.View view, MomentDataSource momentDataSource, LocationDataSource locationDataSource, String userid) {
        super(view, momentDataSource, locationDataSource);
        this.userid = userid;
    }

    @Override
    public void requestNewMoments() {
        isLoading = true;
        momentDataSource.getSomebodyMoments(userid, 0, PAGE_SIZE,
                new MomentDataSource.GetMomentsCallback() {
                    @Override
                    public void onMomentsLoaded(List<Moment> momentList) {
                        view.initMoments(momentList);
                        if (momentList.size() < PAGE_SIZE) {
                            state = END;
                            view.setListEndView();
                        } else {
                            state = LOADING;
                            view.setListLoadingView();
                        }
                        if (isFirst) {
                            isFirst = false;
                            view.showMomentList();
                        }
                        pageNum = 1;
                        isLoading = false;
                        view.hideRefreshing();
                    }

                    @Override
                    public void onDataNotAvailable(String error) {
                        if (NetworkUtils.isLoginFailed(error)) {
                            view.showReminderMessage("登录失效，请重新登录");
                            view.showLoginUI();
                        } else {
                            view.showReminderMessage("获取动态失败，" + error);
                            isLoading = false;
                            view.hideRefreshing();
                        }
                    }
                });
    }

    @Override
    public void requestNextMoments() {
        if (!isLoading && state == LOADING) {
            isLoading = true;
            momentDataSource.getSomebodyMoments(userid, pageNum, PAGE_SIZE,
                    new MomentDataSource.GetMomentsCallback() {
                        @Override
                        public void onMomentsLoaded(List<Moment> momentList) {
                            if (momentList == null) {
                                view.setListEndView();
                            } else {
                                if (momentList.size() < PAGE_SIZE) {
                                    state = END;
                                    view.setListEndView();
                                }
                                view.addMoments(momentList);
                                ++pageNum;
                                isLoading = false;
                            }

                        }

                        @Override
                        public void onDataNotAvailable(String error) {
                            if (NetworkUtils.isLoginFailed(error)) {
                                view.showReminderMessage("登录失效，请重新登录");
                                view.showLoginUI();
                            } else {
                                view.showReminderMessage("获取动态失败，" + error);
                                state = NETWORK_ERROR;
                                view.setListErrorView();
                                isLoading = false;
                            }
                        }
                    });
        }
    }
}
