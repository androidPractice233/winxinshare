package com.scut.weixinshare.manager;

import com.scut.weixinshare.MyApplication;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

//对定位操作进行封装，定位信息通过TencentLocationListener监听器获取
public class LocationManager {

    private static TencentLocationManager manager =
            TencentLocationManager.getInstance(MyApplication.getContext());

    //开始异步定位，获取经纬度及当前位置名，返回监听器注册结果
    public static int startLocation(TencentLocationListener listener) {
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(10000);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
        request.setAllowCache(false);
        return manager.requestLocationUpdates(request, listener);
    }

    //开始异步定位，获取经纬度及当前位置附近的地点名，返回监听器注册结果
    public static int startLocationForPOI(TencentLocationListener listener){
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(10000);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_POI);
        request.setAllowCache(false);
        return manager.requestLocationUpdates(request, listener);
    }

    //停止定位，在不需要定位时必须立即调用
    public static void stopLocation(TencentLocationListener listener) {
        manager.removeUpdates(listener);
    }

}
