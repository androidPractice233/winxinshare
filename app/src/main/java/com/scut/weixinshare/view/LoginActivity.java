package com.scut.weixinshare.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.db.DBOperator;
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
                            //初始化数据库
                            //跳转至个人主页
                            editor.commit();
                            MyApplication.user=user;
                            MyApplication.getInstance().setToken( (String) resultmap.get("token"));
                            MyApplication.getInstance().setUserId((String) ((Map) resultmap.get("user")).get("userId"));
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
}
