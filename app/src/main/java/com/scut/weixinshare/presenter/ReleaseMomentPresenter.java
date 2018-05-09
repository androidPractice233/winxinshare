package com.scut.weixinshare.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.IConst;
import com.scut.weixinshare.contract.ReleaseMomentContract;
import com.scut.weixinshare.manager.LocationManager;
import com.scut.weixinshare.model.Location;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReleaseMomentPresenter implements ReleaseMomentContract.Presenter {

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
    public void result(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    //获取选择的图片
                    addImages(PictureSelector.obtainMultipleResult(data));
                }
                break;
            case IConst.REQUEST_CODE_PICK_LOCATION:
                if(resultCode == Activity.RESULT_OK){
                    setLocation((Location) data.getParcelableExtra("location"));
                }
            default:
                break;
        }
    }

    @Override
    public void start() {
        view.updateLocationStatus(location.getName());
    }

    private void addImages(List<LocalMedia> selectList) {
        selectedImages.clear();
        if(selectList != null && selectList.size() != 0) {
            selectedImages.addAll(selectList);
            List<Uri> pics = new ArrayList<>();
            for (LocalMedia media : selectList) {
                pics.add(Uri.fromFile(new File(media.getPath())));
            }
            view.showAddedPics(pics);
        } else {
            view.showAddedPics(new ArrayList<Uri>());
        }
    }

    private void setLocation(Location location) {
        this.location = location;
        view.updateLocationStatus(location.getName());
    }

}
