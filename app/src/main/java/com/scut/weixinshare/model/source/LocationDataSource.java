package com.scut.weixinshare.model.source;

import com.scut.weixinshare.model.Location;

import java.util.List;

//定位数据来源接口
public interface LocationDataSource {

    //获取定位监听
    interface GetLocationCallback{

        void onSuccess(Location location);

        void onFailure(String error);

    }

    //获取附近POI监听
    interface GetLocationListCallback{

        void onSuccess(List<Location> locationList);

        void onFailure(String error);

    }

    //获取定位信息
    void getLocation(GetLocationCallback callback);

    //获取附近POI
    void getLocationList(GetLocationListCallback callback);

    //清除缓存的定位信息
    void refreshLocation();

}
