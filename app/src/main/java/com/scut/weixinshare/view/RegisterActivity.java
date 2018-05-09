package com.scut.weixinshare.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.R;
import com.scut.weixinshare.db.DBOperator;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.retrofit.BaseCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static String img_url = null;
    private ImageView img_upload;
    private EditText nickname = null;
    private EditText edt_name = null;
    private EditText edt_pwd = null;
    private Button btn_register = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);
        img_upload = findViewById(R.id.profilePhoto);
        edt_name = findViewById(R.id.userName);
        nickname = findViewById(R.id.nickname);
        edt_pwd = findViewById(R.id.password);
        btn_register = findViewById(R.id.registerButton);

        img_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(RegisterActivity.this)
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
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = edt_name.getText().toString();//获取用户名
                final String user_account = nickname.getText().toString();//获取账号
                final String user_pwd = edt_pwd.getText().toString();//获取密码
                if (TextUtils.isEmpty(user_pwd)) {
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(user_name)) {
                    Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user_account.length() > 20) {
                    Toast.makeText(RegisterActivity.this, "您输入的账号过长", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user_pwd.length() > 20) {
                    Toast.makeText(RegisterActivity.this, "您输入的密码过长", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(user_account, user_name, null);
                user.setUserPwd(user_pwd);

                //服务器进行用户注册
                NetworkManager.getInstance().register(new BaseCallback<ResultBean>() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                        ResultBean resultBean = getResultBean(response);
                       if(checkResult(RegisterActivity.this,resultBean)) {
                           //数据库进行用户注册

                          DBOperator dbOperator = new DBOperator();
                           User user = new User(null, user_account, user_name);
                           user.setPortrait(img_url);
                           dbOperator.insertUser(user);
                           dbOperator.close();
                           Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                           startActivity(intent);
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
                try {
                    Uri uri = data.getData();
                    img_url = uri.getPath();
                    ContentResolver cr = RegisterActivity.this.getContentResolver();
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    ImageView imageView = findViewById(R.id.profilePhoto);
                    //将Bitmap设定到ImageView
                    imageView.setImageBitmap(bitmap);
                    NetworkManager.getInstance().MutiprtTest(new Callback<ResultBean>() {
                        @Override
                        public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                            ResultBean resultBean = response.body();
                            Toast.makeText(RegisterActivity.this, (String) resultBean.getData(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResultBean> call, Throwable t) {
                            Log.e("MainActivity", t.getMessage());
                        }
                    }, fileList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}