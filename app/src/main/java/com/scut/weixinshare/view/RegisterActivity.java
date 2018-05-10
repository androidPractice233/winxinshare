package com.scut.weixinshare.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.db.DBOperator;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.LoginReceive;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.retrofit.BaseCallback;
import com.scut.weixinshare.view.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ImageView img_upload;
    private EditText nickname = null;
    private EditText edt_name = null;
    private EditText edt_pwd = null;
    private Button btn_register = null;
//    private EditText edt_sex = null;
    private EditText location = null;
    private File protrait=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        img_upload = findViewById(R.id.profilePhoto);
        edt_name = findViewById(R.id.userName);
//        edt_sex = findViewById(R.id.sex);
        location = findViewById(R.id.location);
        nickname = findViewById(R.id.nickName);
        edt_pwd = findViewById(R.id.password);
        btn_register = findViewById(R.id.registerButton);

        img_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(RegisterActivity.this)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio(
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(false)// 是否可预览图片 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .isZoomAnim(true)// 图片列表点击缩放效果默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = edt_name.getText().toString();
                final String nicknamestr = nickname.getText().toString();
                final String user_pwd = edt_pwd.getText().toString();

//                final String user_sex = edt_sex.getText().toString();
                final String user_loaction = location.getText().toString();
                if (TextUtils.isEmpty(user_pwd)) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(user_name)) {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user_pwd.length() > 20) {
                    Toast.makeText(RegisterActivity.this, "您输入的密码过长", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(null, user_name, nicknamestr);
                user.setUserPwd(user_pwd);
                user.setLocation(user_loaction);


//服务器进行用户注册
                NetworkManager.getInstance().register(new BaseCallback<ResultBean>() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                        ResultBean resultBean = getResultBean(response);
                        Map resultMap= (Map) resultBean.getData();
                        String userid= (String) resultMap.get("userId");

                        if (checkResult(RegisterActivity.this, resultBean)) {
                            if(protrait!=null) {
                                NetworkManager.getInstance().uploadProtrait(new BaseCallback<ResultBean>() {
                                                                                @Override
                                                                                public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                                                                                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                                    startActivity(intent);
                                                                                }

                                                                                @Override
                                                                                public void onFailure(Call<ResultBean> call, Throwable t) {
                                                                                    t.printStackTrace();
                                                                                }


                                                                            }
                                        , userid, protrait);
                            }
                            //没有上传图片，正确注册
                            else {
                                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    }


                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {
                        Log.e("register", t.getMessage());
                    }
                }, user);

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<File> fileList = new ArrayList<>();
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                for (LocalMedia p : selectList) {
                    fileList.add(new File(p.getPath()));
                }
                    ImageView imageView = findViewById(R.id.profilePhoto);
                    if(fileList.size()>0) {
                        Glide.with(this).load(fileList.get(0)).into(imageView);
                        protrait=fileList.get(0);
                    }
//                    NetworkManager.getInstance().MutiprtTest(new Callback<ResultBean>() {
//                        @Override
//                        public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
//                            ResultBean resultBean = response.body();
//                            Toast.makeText(RegisterActivity.this, (String) resultBean.getData(), Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResultBean> call, Throwable t) {
//                            Log.e("MainActivity", t.getMessage());
//                        }
//                    }, fileList);

            }
        }
    }

}