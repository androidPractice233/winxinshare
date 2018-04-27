package com.scut.weixinshare.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.IConst;
import com.scut.weixinshare.R;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.retrofit.BaseCallback;
import com.scut.weixinshare.utils.LocationUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button btnPopPhoto;
    Button button;
    Button locationBtn;
    Button toHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleLocationPermission();

        toHome = findViewById(R.id.button_to_home_activity);
        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.testButton);
        btnPopPhoto = findViewById(R.id.btnPopPhoto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkManager.getInstance().test(new BaseCallback() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                        ResultBean resultBean=  response.body();
                        if(this.checkResult(MainActivity.this,resultBean)) {
                            Toast.makeText(MainActivity.this, (String) resultBean.getData(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {
                        Log.e("MainActivity", t.getMessage()  );
                    }
                });
            }
        });

        btnPopPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(MainActivity.this)
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

        locationBtn=(Button)findViewById(R.id.btn_loca);
        final TextView text = (TextView) findViewById( R.id.text );
        locationBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = LocationUtils.getInstance( MainActivity.this ).returnLocation();
                if (location != null) {
                    String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
                    Log.d( "LocationUtils", address );
                    text.setText( address );
                }
                else
                    Log.d("LocationUtils", "无法获得位置类");
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<File> fileList=new ArrayList<>();
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
//                StringBuilder sb = new StringBuilder();

                for (LocalMedia p : selectList) {
//                    sb.append(p);
//                    sb.append("\n");
                    fileList.add( new File(p.getPath()));
                }
                try {
                    NetworkManager.getInstance().MutiprtTest(new BaseCallback() {
                        @Override
                        public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {

                            ResultBean resultBean=  response.body();
                            if(this.checkResult(MainActivity.this,resultBean)) {
                                Toast.makeText(MainActivity.this, (String) resultBean.getData(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultBean> call, Throwable t) {
                            Log.e("MainActivity", t.getMessage()  );
                        }
                    },fileList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //yjPublishEdit.setText(sb.toString());
            }
        }
    }

    //申请获取权限后回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case IConst.REQUEST_LOCATION: {
                //允许获取地理位置权限
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("需要定位权限")
                            .setMessage("我们需要获取您的位置信息来获取、发布动态，" +
                                    "请前往设置界面手动开启定位权限")
                            .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    intent.setData(Uri.fromParts("package", getPackageName(),
                                            null));
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }

            }
        }
    }

    private void handleLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    permission[0])) {
                //当用户曾经拒绝掉权限时
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("需要定位权限")
                        .setMessage("我们需要获取您的位置信息来获取、发布动态，" +
                                "请在接下来弹出的对话框中选择“允许”。")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        permission,
                                        IConst.REQUEST_LOCATION);
                            }
                        })
                        .setCancelable(false)
                        .show();

            } else {
                //未申请过权限或用户在拒绝权限时勾选了“不再提醒”选项
                ActivityCompat.requestPermissions(MainActivity.this,
                        permission,
                        IConst.REQUEST_LOCATION);
            }
        }
    }
}
