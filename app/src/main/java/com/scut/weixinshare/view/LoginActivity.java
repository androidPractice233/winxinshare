package com.scut.weixinshare.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by skyluo on 2018/4/15.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
//        SharedPreferences sharedPreferences = getSharedPreferences("weixinshare", Context.MODE_PRIVATE); //私有数据
//        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
//        editor.putString("token", token);
//        editor.putInt("age", 4);
//        editor.commit();//提交修改
        return super.clone();
    }
}
