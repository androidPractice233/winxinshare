package com.scut.weixinshare.model.source;

import com.scut.weixinshare.manager.LocationManager;
import com.scut.weixinshare.model.Location;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentPoi;

import java.util.ArrayList;
import java.util.List;

//实现位置信息仓库
public class LocationRepository implements LocationDataSource {

    private static LocationRepository INSTANCE = new LocationRepository();
    private Location lastLocation;     //位置信息缓存

    //单例，线程安全
    public static LocationRepository getInstance(){
        return INSTANCE;
    }

    @Override
    public void getLocation(final GetLocationCallback callback) {
        if(lastLocation != null){
            callback.onSuccess(lastLocation);
        } else {
            if(LocationManager.startLocation(
                    new TencentLocationListener() {
                @Override
                public void onLocationChanged(TencentLocation tencentLocation, int error,
                                              String reason) {
                    if(TencentLocation.ERROR_OK == error) {
                        //更新位置信息缓存
                        lastLocation = new Location(tencentLocation);
                        callback.onSuccess(lastLocation);
                    } else {
                        callback.onFailure(reason);
                    }
                    //无论定位是否成功，都要停止定位
                    LocationManager.stopLocation(this);
                }

                @Override
                public void onStatusUpdate(String s, int i, String s1) {

                }
            }) != 0){
                //listener注册失败
                callback.onFailure("定位组件加载失败");
            }
        }
    }

    @Override
    public void getLocationList(final GetLocationListCallback callback) {
        if(LocationManager.startLocationForPOI(new TencentLocationListener() {
            @Override
            public void onLocationChanged(TencentLocation tencentLocation, int error,
                                          String reason) {
                if(TencentLocation.ERROR_OK == error){
                    List<Location> locationList = new ArrayList<>();
                    for(TencentPoi poi : tencentLocation.getPoiList()){
                        locationList.add(new Location(poi));
                    }
                    callback.onSuccess(locationList);
                } else {
                    callback.onFailure(reason);
                }
                //无论定位是否成功，都要停止定位
                LocationManager.stopLocation(this);
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1) {

            }
        }) != 0){
            //listener注册失败
            callback.onFailure("定位组件加载失败");
        }
    }

    @Override
    public void refreshLocation(){
        //清除位置信息缓存
        lastLocation = null;
    }

}
