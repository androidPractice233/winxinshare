package com.scut.weixinshare.presenter;

import android.net.Uri;

import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.contract.ReleaseMomentContract;
import com.scut.weixinshare.manager.LocationManager;
import com.scut.weixinshare.model.Location;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReleaseMomentPresenter implements ReleaseMomentContract.Presenter,
        TencentLocationListener {

    private ReleaseMomentContract.View view;
    private Location location;
    private List<LocalMedia> selectedImages = new ArrayList<>();

    public ReleaseMomentPresenter(ReleaseMomentContract.View view, Location location){
        this.view = view;
        this.location = location;
        view.setPresenter(this);
    }

    @Override
    public void getLocation() {
        /*view.showLoadingDialog("正在更新定位信息");
        int error = LocationManager.startLocation(this);
        if (error != 0) {
            view.hideLoadingDialog();
            view.showReminderMessage("定位组件加载失败，错误码" + error);
        }*/
        view.showPickLocationUI();
    }

    @Override
    public void selectImages() {
        view.showPictureSelectorUI(selectedImages);
    }

    @Override
    public void publish(String text) {
        if(text != null && !"".equals(text)){
            //发送动态
            List<File> imageFileList = null;
            if(selectedImages.size() != 0) {
                imageFileList = new ArrayList<>();
                for (LocalMedia image : selectedImages) {
                    imageFileList.add(new File(image.getPath()));
                }
            }
            view.setResultAndFinish(text, location, imageFileList);
        } else {
            view.showReminderMessage("输入内容不能为空");
        }
    }

    @Override
    public void addImages(List<LocalMedia> selectList) {
        if(selectList != null && selectList.size() != 0) {
            selectedImages.addAll(selectList);
            List<Uri> pics = new ArrayList<>();
            for (LocalMedia media : selectList) {
                pics.add(Uri.fromFile(new File(media.getPath())));
            }
            view.showAddedPics(pics);
        }
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
        view.updateLocationStatus(location.getName());
    }

    @Override
    public void start() {
        view.updateLocationStatus(location.getName());
    }

    @Override
    public void onLocationChanged(final TencentLocation location, final int error, final String reason) {
        view.hideLoadingDialog();
        if (TencentLocation.ERROR_OK == error) {
            this.location.setLongitude(location.getLongitude());
            this.location.setLatitude(location.getLatitude());
            this.location.setName(location.getName());
            view.updateLocationStatus(location.getName());
            view.showReminderMessage("定位成功");
        } else {
            if (reason != null && !"".equals(reason)) {
                view.showReminderMessage("定位失败，" + reason);
            } else {
                view.showReminderMessage("定位失败，错误码：" + error);
            }
        }
        LocationManager.stopLocation(this);
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }
}
