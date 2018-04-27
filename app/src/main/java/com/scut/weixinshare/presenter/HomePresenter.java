package com.scut.weixinshare.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.scut.weixinshare.contract.HomeContract;
import com.scut.weixinshare.manager.LocationManager;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomePresenter implements HomeContract.Presenter, TencentLocationListener {
    private HomeContract.View view;
    private TencentLocation location = null;
    private boolean isFirst = true;
    private int count = 0;
    private boolean isLoading = false;
    //private List<Moment> momentList = new ArrayList<>();

    public HomePresenter(HomeContract.View view){
        this.view = view;
        view.setPresenter(this);
    }

    //测试用数据
    private List<Moment> initTestMoments(){
        List<Moment> momentList = new ArrayList<>();
        for(int i = 0; i < 25; ++i){
            List<Comment> comments = new ArrayList<>();
            comments.add(new Comment(UUID.randomUUID(), "CJJ",
                            "草鱼", "操他妈你不要再讲了",
                            new Timestamp(System.currentTimeMillis())));
            comments.add(new Comment(UUID.randomUUID(), "草鱼",
                    "CJJ", "甘霖娘", new Timestamp(System.currentTimeMillis())));
            comments.add(new Comment(UUID.randomUUID(), "CJJ",
                    "草鱼", "好了你不要再讲了",
                    new Timestamp(System.currentTimeMillis())));
            comments.add(new Comment(UUID.randomUUID(), "草鱼",
                    "CJJ", "鸡掰", new Timestamp(System.currentTimeMillis())));
            comments.add(new Comment(UUID.randomUUID(), "CJJ",
                    "草鱼", "诶？不可以这样子讲诶，这个是脏话，小孩子不可以讲",
                    new Timestamp(System.currentTimeMillis())));
            comments.add(new Comment(UUID.randomUUID(), "草鱼",
                    null, "甘霖娘", new Timestamp(System.currentTimeMillis())));
            List<Uri> picUris = null;
            if(i % 3 == 0){
                picUris = new ArrayList<>();
                picUris.add(Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524488493343&di=997dd503578a6374d13626d953df73f9&imgtype=0&src=http%3A%2F%2Fi2.hdslb.com%2Fbfs%2Farchive%2F9892e6f032425fc9e9831fa1ed855318c12702ad.jpg"));
            } else if(i % 4 == 0){
                picUris = new ArrayList<>();
                picUris.add(Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524488493343&di=997dd503578a6374d13626d953df73f9&imgtype=0&src=http%3A%2F%2Fi2.hdslb.com%2Fbfs%2Farchive%2F9892e6f032425fc9e9831fa1ed855318c12702ad.jpg"));
                picUris.add(Uri.parse("http://img5.imgtn.bdimg.com/it/u=2941775728,3357653807&fm=27&gp=0.jpg"));
                picUris.add(Uri.parse("http://www.dsuu.cc/wp-content/uploads/2016/06/meizitu-74.jpg"));
            } else if(i % 5 == 0){
                picUris = new ArrayList<>();
                picUris.add(Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524488493343&di=997dd503578a6374d13626d953df73f9&imgtype=0&src=http%3A%2F%2Fi2.hdslb.com%2Fbfs%2Farchive%2F9892e6f032425fc9e9831fa1ed855318c12702ad.jpg"));
                picUris.add(Uri.parse("http://img5.imgtn.bdimg.com/it/u=2941775728,3357653807&fm=27&gp=0.jpg"));
                picUris.add(Uri.parse("http://img0.imgtn.bdimg.com/it/u=3788500599,1780671145&fm=27&gp=0.jpg"));
                picUris.add(Uri.parse("http://www.dsuu.cc/wp-content/uploads/2016/06/meizitu-74.jpg"));
            }
            momentList.add(new Moment(UUID.randomUUID(), "jjboom", "傻强",
                    Uri.parse("https://img3.duitang.com/uploads/item/201604/01/20160401215443_tYJne.jpeg"),
                     new Timestamp(System.currentTimeMillis()), "C12 224",
                    "嘿嘿嘿", picUris, comments, new Timestamp(System.currentTimeMillis())));
        }
        return momentList;
    }

    @Override
    public void getLocation() {
        LocationManager.startLocation(HomePresenter.this);
    }

    @Override
    public void requestNewMoments() {
        isLoading = true;
        //momentList = initTestMoments();
        //view.initMoments(momentList);
        view.initMoments(initTestMoments());
        view.hideRefreshing();
        isLoading = false;
    }

    @Override
    public void requestNextMoments() {
        if(count == 5){
            view.setListEndView();
        } else if(!isLoading){
            isLoading = true;
            view.setListLoadingView();
            List<Moment> addedMoments = initTestMoments();
            //momentList.addAll(addedMoments);
            view.addMoments(addedMoments);
            ++count;
            isLoading = false;
        }
    }

    @Override
    public void toReleaseMoment() {
        if(location != null){
            //已获得定位信息的情况下才可以发布动态
            view.showReleaseMomentUI(new Location(location));
        } else {
            view.showReminderMessage("缺少定位信息");
        }
    }

    @Override
    public void toMomentDetail(Moment moment) {
        view.showMomentDetailUI(moment, false);
    }

    @Override
    public void toReleaseComment(Moment moment) {
        view.showMomentDetailUI(moment, true);
    }

    @Override
    public void start() {
        getLocation();
        if(isFirst) {
            view.showRefreshing();
            requestNewMoments();
            isFirst = false;
        }
    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        if(TencentLocation.ERROR_OK == error){
            this.location = location;
        } else {
            view.showReminderMessage("定位失败");
        }
        LocationManager.stopLocation(this);
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        //处理GPS、Wifi等状态变化时间
    }
}
