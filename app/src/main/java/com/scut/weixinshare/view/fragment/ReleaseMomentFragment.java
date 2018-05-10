package com.scut.weixinshare.view.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.IConst;
import com.scut.weixinshare.R;
import com.scut.weixinshare.adapter.ImageAdapter;
import com.scut.weixinshare.contract.ReleaseMomentContract;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.utils.ToastUtils;
import com.scut.weixinshare.view.PickLocationActivity;
import com.scut.weixinshare.view.component.NineGridPatternView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//发布动态fragment
public class ReleaseMomentFragment extends Fragment implements ReleaseMomentContract.View,
        View.OnClickListener {

    private TextView locationStatus;        //显示位置信息
    private EditText text;                  //文字信息编辑
    private ProgressDialog dialog;          //加载中对话框
    private NineGridPatternView images;
    private ImageAdapter adapter;
    private ReleaseMomentContract.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_release_moment, container,
                false);
        ImageButton relocateButton = view.findViewById(R.id.relocate);
        relocateButton.setOnClickListener(this);
        ImageButton addPicButton = view.findViewById(R.id.add_pics);
        addPicButton.setOnClickListener(this);
        ImageButton publishButton = view.findViewById(R.id.publish);
        publishButton.setOnClickListener(this);
        locationStatus = view.findViewById(R.id.location);
        text = view.findViewById(R.id.text_resource);
        images = view.findViewById(R.id.pics_resource);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null) {
            presenter.start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode, data);
    }

    @Override
    public void showLoadingDialog(String text) {
        dialog = new ProgressDialog(getContext());
        dialog.setTitle(text);
        dialog.setMessage("加载中……");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void hideLoadingDialog(){
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void updateLocationStatus(String location) {
        locationStatus.setText(location);
    }

    @Override
    public void showAddedPics(List<Uri> pics) {
        images.setView(pics);
    }

    @Override
    public void showReminderMessage(String text) {
        ToastUtils.showToast(getContext(), text);
    }

    @Override
    public String getText() {
        return text.getText().toString();
    }

    @Override
    public void showPickLocationUI() {
        PickLocationActivity.activityStartForResult(this);
    }

    @Override
    public void showPictureSelectorUI(List<LocalMedia> selected) {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.PictureSelectorTheme)
                .maxSelectNum(IConst.MAX_IMAGE_NUM)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .isCamera(false)
                .openClickSound(false)
                .selectionMedia(selected)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    public void setResultAndFinish(String text, Location location, List<File> images) {
        Intent intent = new Intent();
        intent.putExtra("text", text);
        intent.putExtra("location", location);
        if(images == null){
            intent.putExtra("isTextOnly", true);
        } else {
            intent.putExtra("isTextOnly", false);
            intent.putExtra("images", images.toArray(new File[images.size()]));
        }
        if(getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    @Override
    public void setPresenter(ReleaseMomentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relocate:
                presenter.getLocation();
                break;
            case R.id.add_pics:
                presenter.selectImages();
                break;
            case R.id.publish:
                presenter.publish(text.getText().toString());
                break;
            default:
                break;
        }
    }

}
