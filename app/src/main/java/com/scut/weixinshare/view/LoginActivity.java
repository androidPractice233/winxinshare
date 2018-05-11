package com.scut.weixinshare.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.db.DBOperator;
import com.scut.weixinshare.db.MyDBHelper;
import com.scut.weixinshare.db.Test;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.retrofit.BaseCallback;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by skyluo on 2018/4/15.
 */

public class LoginActivity extends AppCompatActivity {

    private ImageView img_upload;
    private EditText edt_account = null;
    private EditText edt_name = null;
    private EditText edt_pwd = null;
    private Button btn_login = null;
    private Button register_button = null;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Inflate the layout for this userFragment
        setContentView(R.layout.fragment_login);

        img_upload = findViewById(R.id.profilePhoto);
        edt_name = findViewById(R.id.userName);
        edt_pwd = findViewById(R.id.password);
        btn_login = findViewById(R.id.loginButton);
        register_button=findViewById(R.id.registerButton);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = edt_name.getText().toString();//获取用户名
                final String user_pwd = edt_pwd.getText().toString();//获取密码

                if(TextUtils.isEmpty(user_pwd)){
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(user_name)){
                    Toast.makeText(LoginActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                user =new User();
                user.setUserPwd(user_pwd);
                user.setUserName(user_name);
                NetworkManager.getInstance().login(new BaseCallback() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {

                        ResultBean resultBean=  getResultBean(response);
                        if(checkResult(LoginActivity.this,resultBean)) {
                            SharedPreferences preferences = MyApplication.getInstance().getApplicationContext()
                                    .getSharedPreferences("weixinshare", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            Map resultmap = (Map) resultBean.getData();
                            editor.putString("token", (String) resultmap.get("token"));

                            editor.commit();
                            user.setUserId((String) ((Map) resultmap.get("user")).get("userId"));
                            user.setNickName((String) ((Map) resultmap.get("user")).get("nickName"));
                            //user.setSex(((Map) resultmap.get("user")).get("sex").toString());
                            user.setSex(1);
                            user.setLocation((String) ((Map) resultmap.get("user")).get("location"));
                            user.setBirthday(((Map) resultmap.get("user")).get("birthday")+"");
                            user.setPortrait((String) ((Map) resultmap.get("user")).get("portrait"));
                            MyApplication.user=user;
                            double d = (Double) ((Map) resultmap.get("user")).get("birthday");
                            String ss = d+"";
                            double dd = Double.parseDouble(ss);
                            Long l = Math.round(dd);
                            Log.d("testTimeFomat",l+"");

                            MyApplication.getInstance().setToken( (String) resultmap.get("token"));
                            MyApplication.getInstance().setUserId((String) ((Map) resultmap.get("user")).get("userId"));

                            //初始化数据库
                            //new Test(LoginActivity.this);
                            MyDBHelper.DB_NAME = MyApplication.user.getUserId();
                            if(MyDBHelper.DB_NAME==null){
                                Toast.makeText(LoginActivity.this,"还没登录吧",Toast.LENGTH_LONG).show();
                            }
                            MyDBHelper myDBHelper = new MyDBHelper(LoginActivity.this,1);
                            myDBHelper.close();

                            //更新用户资料
                            DBOperator dbOperator = new DBOperator();
                            dbOperator.updateUser(user);
                            dbOperator.close();


                            //跳转至个人主页
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            ////
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {

                    }
                },user);


            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        handleLocationPermi();
    }

    //申请获取权限后回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case IConst.REQUEST_LOCATION:{
                //允许获取地理位置权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                else {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("需要定位权限")
                            .setMessage("应用需要获取您的位置信息，请前往设置界面手动开启定位权限")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();

                }

            }
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);

    }
private Boolean checkLogin(){

    SharedPreferences preferences = MyApplication.getInstance().getApplicationContext().getSharedPreferences("weixinshare",Context.MODE_PRIVATE);
    String token = preferences.getString("token",null);
    if(token==null) return false;
    else return true;
}
    public static void relogin(Context context){
        MyApplication.getInstance().setToken(null);
        SharedPreferences preferences = MyApplication.getInstance().getApplicationContext().getSharedPreferences("weixinshare",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("token");
        editor.commit();
        Intent intent=new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    private void handleLocationPermi(){
        //当用户拒绝掉权限时.
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)||
                ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("需要定位权限")
                    .setMessage("应用需要获取您的位置信息，请在接下来的对话框中选择“允许”")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(LoginActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            IConst.REQUEST_LOCATION);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    IConst.REQUEST_LOCATION);
        }
    }
}
