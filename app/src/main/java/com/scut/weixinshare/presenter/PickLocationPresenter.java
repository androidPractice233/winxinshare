package com.scut.weixinshare.presenter;

import com.scut.weixinshare.contract.PickLocationContract;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.source.LocationDataSource;

import java.util.List;

public class PickLocationPresenter implements PickLocationContract.Presenter {

    private PickLocationContract.View view;
    private LocationDataSource locationDataSource;

    public PickLocationPresenter(PickLocationContract.View view, LocationDataSource locationDataSource){
        this.view = view;
        view.setPresenter(this);
        this.locationDataSource = locationDataSource;
    }

    @Override
    public void start() {
        locationDataSource.getLocationList(new LocationDataSource.GetLocationListCallback() {
            @Override
            public void onSuccess(List<Location> locationList) {
                view.setView(locationList);
            }

            @Override
            public void onFailure(String error) {
                view.showReminderMessage(error);
            }
        });
    }

}
