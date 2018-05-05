package com.scut.weixinshare.model.source;

import com.scut.weixinshare.model.Location;

import java.util.List;

public interface LocationDataSource {

    interface GetLocationCallback{

        void onSuccess(Location location);

        void onFailure(String error);

    }

    interface GetLocationListCallback{

        void onSuccess(List<Location> locationList);

        void onFailure(String error);

    }

    void getLocation(GetLocationCallback callback);

    void getLocationList(GetLocationListCallback callback);

    void setLocation(Location location);

    void refreshLocation();

}
