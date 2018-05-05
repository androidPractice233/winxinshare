package com.scut.weixinshare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentPoi;

import java.io.Serializable;

//位置信息封装，可以在Activity间传递
public class Location implements Parcelable {

    private double longitude;     //经度
    private double latitude;      //纬度
    private String name;          //位置名
    private String address;       //地址

    public Location(TencentLocation location){
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.name = location.getName();
        this.address = location.getAddress();
    }

    public Location(TencentPoi poi){
        this.longitude = poi.getLongitude();
        this.latitude = poi.getLatitude();
        this.name = poi.getName();
        this.address = poi.getAddress();
    }

    private Location(){
        this(0.0, 0.0, null, null);
    }

    public Location(double longitude, double latitude, String name, String address){
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeString(name);
        parcel.writeString(address);
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>(){

        @Override
        public Location createFromParcel(Parcel parcel) {
            Location location = new Location();
            location.longitude = parcel.readDouble();
            location.latitude = parcel.readDouble();
            location.name = parcel.readString();
            location.address = parcel.readString();
            return location;
        }

        @Override
        public Location[] newArray(int i) {
            return new Location[i];
        }
    };

}
