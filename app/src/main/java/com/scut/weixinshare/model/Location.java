package com.scut.weixinshare.model;

import com.tencent.map.geolocation.TencentLocation;

import java.io.Serializable;

//位置信息封装，可以在Activity间传递
public class Location implements Serializable {

    private double longitude;     //经度
    private double latitude;      //纬度
    private String name;          //位置名

    public Location(TencentLocation location){
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.name = location.getName();
    }

    public Location(double longitude, double latitude, String name){
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
