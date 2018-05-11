package com.scut.weixinshare.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.contract.HomeContract;
import com.scut.weixinshare.manager.LocationManager;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.source.LocationDataSource;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.MomentDataSource;
import com.scut.weixinshare.model.source.MomentsRepository;
import com.scut.weixinshare.utils.NetworkUtils;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class HomePresenter implements HomeContract.Presenter {

    protected static int PAGE_SIZE = 25;

    protected static final int NETWORK_ERROR = 0;
    protected static final int LOADING = 1;
    protected static final int END = 2;

    protected int state = LOADING;
    protected HomeContract.View view;
    protected boolean isFirst = true;
    protected int pageNum = 1;
    protected boolean isLoading = false;
    protected MomentDataSource momentDataSource;
    private LocationDataSource locationDataSource;
    private int lastPosition = -1;
    private String lastMomentId;

    public HomePresenter(HomeContract.View view, MomentDataSource momentDataSource,
                         LocationDataSource locationDataSource){
        this.view = view;
        this.momentDataSource = momentDataSource;
        this.locationDataSource = locationDataSource;
        view.setPresenter(this);
    }

    @Override
    public void requestNewMoments() {
        isLoading = true;
        locationDataSource.refreshLocation();
        locationDataSource.getLocation(new LocationDataSource.GetLocationCallback() {
            @Override
            public void onSuccess(Location location) {
                momentDataSource.getMoments(location, 0, PAGE_SIZE,
                        new MomentDataSource.GetMomentsCallback(){
                    @Override
                    public void onMomentsLoaded(List<Moment> momentList) {
                        view.initMoments(momentList);
                        if(momentList.size() < PAGE_SIZE){
                            state = END;
                            view.setListEndView();
                        } else {
                            state = LOADING;
                            view.setListLoadingView();
                        }
                        if(isFirst){
                            isFirst = false;
                            view.showMomentList();
                        }
                        pageNum = 1;
                        isLoading = false;
                        view.hideRefreshing();
                    }

                    @Override
                    public void onDataNotAvailable(String error) {
                        if(NetworkUtils.isLoginFailed(error)){
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
            public void onFailure(String error) {
                view.showReminderMessage("获取定位失败，" + error);
                isLoading = false;
                view.hideRefreshing();
            }
        });
    }

    @Override
    public void requestNextMoments() {
        if(!isLoading && state == LOADING){
            isLoading = true;
            locationDataSource.getLocation(new LocationDataSource.GetLocationCallback(){

                @Override
                public void onSuccess(Location location) {
                    momentDataSource.getMoments(location, pageNum, PAGE_SIZE,
                            new MomentDataSource.GetMomentsCallback() {
                        @Override
                        public void onMomentsLoaded(List<Moment> momentList) {
                            if(momentList == null) {
                                view.setListEndView();
                            } else {
                                if(momentList.size() < PAGE_SIZE){
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
                            if(NetworkUtils.isLoginFailed(error)){
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

                @Override
                public void onFailure(String error) {
                    //理论上不会出现
                    state = NETWORK_ERROR;
                    view.setListErrorView();
                    view.showReminderMessage("获取定位失败，" + error);
                    isLoading = false;
                }
            });
        }
    }

    @Override
    public void editReleaseMoment() {
        locationDataSource.getLocation(new LocationDataSource.GetLocationCallback() {
            @Override
            public void onSuccess(Location location) {
                view.showReleaseMomentUI(location);
            }

            @Override
            public void onFailure(String error) {
                view.showReminderMessage("缺少位置信息，无法发送动态");
            }
        });
    }

    @Override
    public void openMomentDetail(Moment moment, int position) {
        lastPosition = position;
        lastMomentId = moment.getMomentId();
        view.showMomentDetailUI(lastMomentId, false);
    }

    @Override
    public void releaseComment(Moment moment, int position) {
        lastPosition = position;
        lastMomentId = moment.getMomentId();
        view.showMomentDetailUI(lastMomentId, true);
    }

    @Override
    public void releaseMoment(String text, Location location) {
        view.showReminderMessage("正在发送动态");
        momentDataSource.createMoment(MyApplication.getInstance().getUserId(), text, location,
                new MomentDataSource.CreateMomentCallback() {
                    @Override
                    public void onSuccess() {
                        view.showReminderMessage("动态发送成功");
                    }

                    @Override
                    public void onFailure(String error) {
                        if(NetworkUtils.isLoginFailed(error)){
                            view.showReminderMessage("登录失效，请重新登录");
                            view.showLoginUI();
                        } else {
                            view.showReminderMessage("动态发送失败，" + error);
                        }
                    }
                });
    }

    @Override
    public void releaseMoment(String text, Location location, List<File> images) {
        view.showReminderMessage("正在发送动态");
        momentDataSource.createMoment(MyApplication.getInstance().getUserId(), text, location, images,
                new MomentDataSource.CreateMomentCallback() {
                    @Override
                    public void onSuccess() {
                        view.showReminderMessage("动态发送成功");
                    }

                    @Override
                    public void onFailure(String error) {
                        if(NetworkUtils.isLoginFailed(error)){
                            view.showReminderMessage("登录失效，请重新登录");
                            view.showLoginUI();
                        } else {
                            view.showReminderMessage("动态发送失败，" + error);
                        }
                    }
                });
    }

    @Override
    public void breakErrorState() {
        state = LOADING;
        view.setListLoadingView();
    }

    @Override
    public void openUserData(Moment moment, int position) {
        view.showUserDataUI(moment.getUserId());
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IConst.REQUEST_CODE_MOMENT_DETAIL:
                if (resultCode == RESULT_OK) {
                    //获取动态正文页面返回的修改信息
                    if(data.getBooleanExtra("isChanged", false) &&
                            lastPosition != -1){
                        momentDataSource.getMoment(lastMomentId, new MomentDataSource.GetMomentCallback() {
                            @Override
                            public void onMomentLoaded(Moment moment) {
                                view.updateMomentView(moment, lastPosition);
                            }

                            @Override
                            public void onDataNotAvailable(String error) {
                                if(NetworkUtils.isLoginFailed(error)){
                                    view.showReminderMessage("登录失效，请重新登录");
                                    view.showLoginUI();
                                }
                            }
                        });
                    }
                }
                break;
            case IConst.REQUEST_CODE_RELEASE_MOMENT:
                if(resultCode == RESULT_OK){
                    //发送动态
                    String text = data.getStringExtra("text");
                    Location location = data.getParcelableExtra("location");
                    if(!data.getBooleanExtra("isTextOnly", true)){
                        File[] images = (File[]) data.getSerializableExtra("images");
                        releaseMoment(text, location, new ArrayList<>(Arrays
                                .asList(images)));
                    } else {
                        releaseMoment(text, location);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void openBigImages(List<Uri> uriList, int position) {
        ArrayList<Uri> list = new ArrayList<>();
        for(int i = position; i < uriList.size(); ++i){
            list.add(uriList.get(i));
        }
        for(int i = 0; i < position; ++i){
            list.add(uriList.get(i));
        }
        view.showBigPicUI(list);
    }

    @Override
    public void start() {
        if(isFirst) {
            view.hideMomentList();
            view.showRefreshing();
            requestNewMoments();
        }
    }

}
