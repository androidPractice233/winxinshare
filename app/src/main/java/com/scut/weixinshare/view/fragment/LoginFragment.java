package com.scut.weixinshare.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.scut.weixinshare.db.MyDBHelper;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.view.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {

    private ImageView img_upload;
    private EditText edt_account = null;
    private EditText edt_name = null;
    private EditText edt_pwd = null;
    private Button btn_login = null;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        img_upload = view.findViewById(R.id.profilePhoto);
        edt_name = view.findViewById(R.id.userName);
        edt_account = view.findViewById(R.id.account);
        edt_pwd = view.findViewById(R.id.password);
        btn_login = view.findViewById(R.id.loginButton);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = edt_name.getText().toString();//获取用户名
                final String user_account = edt_account.getText().toString();//获取账号
                final String user_pwd = edt_pwd.getText().toString();//获取密码
                if(TextUtils.isEmpty(user_account)){
                    Toast.makeText(getContext(),"账号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(user_pwd)){
                    Toast.makeText(getContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(user_name)){
                    Toast.makeText(getContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(user_account.length()>20){
                    Toast.makeText(getContext(),"您输入的账号过长",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(user_pwd.length()>20){
                    Toast.makeText(getContext(),"您输入的密码过长",Toast.LENGTH_SHORT).show();
                    return;
                }

                //查找有无此用户
                DBOperator operator = new DBOperator();
                User user = operator.selectUser(user_account);
                NetworkManager.getInstance().login(new Callback<ResultBean>() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                        ResultBean resultBean=  response.body();
                        SharedPreferences preferences = MyApplication.getInstance().getApplicationContext()
                                .getSharedPreferences("weixinshare",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token",resultBean.getData().toString());
                        //初始化数据库

                        //跳转至个人主页
                        Intent intent = new Intent();
                        ////
                    }

                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {

                    }
                },user);
            }
        });



        return view;
    }



}
