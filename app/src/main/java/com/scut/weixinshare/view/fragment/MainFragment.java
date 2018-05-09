package com.scut.weixinshare.view.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.R;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.retrofit.BaseCallback;
import com.scut.weixinshare.view.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by skyluo on 2018/4/16.
 */

public class MainFragment  extends Fragment{
    private Button button;
    private Button btnPopPhoto;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_main,container,false);
        button= (Button) view.findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkManager.getInstance().test(new BaseCallback() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                       ResultBean resultBean=getResultBean(response);
                        if(this.checkResult(getContext(),resultBean)) {
//                           Toast.makeText(getContext(), (String) resultBean.getData(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {
                        Log.e("MainActivity", t.getMessage()  );
                    }
                });
            }
        });
        btnPopPhoto=(Button) view.findViewById(R.id.btnPopPhoto);
        btnPopPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(MainFragment.this)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio(
                        .maxSelectNum(9)// 最大图片选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(false)// 是否可预览图片 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        });
        return  view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<File> fileList=new ArrayList<>();
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
//                StringBuilder sb = new StringBuilder();

                for (LocalMedia p : selectList) {
//                    sb.append(p);
//                    sb.append("\n");
                    fileList.add( new File(p.getPath()));
                }
                try {
                        NetworkManager.getInstance().uploadMomentImages(new Callback<ResultBean>() {
                            @Override
                            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                                ResultBean resultBean=  response.body();
                                Toast.makeText(getContext(),(String)resultBean.getData(),Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<ResultBean> call, Throwable t) {
                                Log.e("MainActivity", t.getMessage()  );
                            }
                        },"f892c315e2174ef7ba5d057606ee008e",fileList);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //yjPublishEdit.setText(sb.toString());
            }
        }
    }

}

