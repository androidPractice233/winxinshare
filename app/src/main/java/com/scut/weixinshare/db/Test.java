package com.scut.weixinshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.view.MainActivity;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Test {
    private Context context;
    public Test(Context context){
        this.context = context;
        //设置数据库名字
        MyDBHelper.DB_NAME = "zhanepng";
        //初始化数据库
        MyDBHelper myDBHelper = new MyDBHelper(context,1);
        myDBHelper.close();
    }

    public void testDb(){


        DBOperator dbOperator = new DBOperator();
        dbOperator.insertUser(new User("0001","zhanpeng","pengpeng"));
        dbOperator.insertMoment(new Moment("0001","0001","20180501","20180501","gz"));
        for(int i=0;i<10;i++)
            dbOperator.insertComment(new Comment("0001"+i,"0001","0001","0001","20180501","hahaha"));
        String time = dbOperator.getLastTime();
        Toast.makeText(context,time,Toast.LENGTH_LONG).show();
        Moment moment =  dbOperator.selectMoment("0001");
        if(moment!=null){
            //Toast.makeText(context,moment.getCreateTime(),Toast.LENGTH_LONG).show();
        }
        dbOperator.close();

    }
    public void login(){
        NetworkManager.getInstance().test(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                if(resultBean==null){
                    Log.d("loginRes","resultBean is null");
                    Test.this.login();
                    return;
                }
                Log.d("loginRes","200");
                //解析数据
                String data = resultBean.getData().toString();
                if(data!=null) {
                    Log.d("loginRes", data);
                    MainActivity.TOKEN = getToken(data);
                    MainActivity.USERID = getUserId(data);
                    Log.d("loginRes", MainActivity.TOKEN);
                    Log.d("loginRes", MainActivity.USERID);


                    SharedPreferences sharedPreferences = context.getSharedPreferences("weixinshare", Context.MODE_PRIVATE); //私有数据
                    SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                    editor.putString("token", MainActivity.TOKEN);
                    editor.commit();//提交修改
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                Log.d("loginRes",t.getMessage());
                Test.this.login();
            }

            String getToken(String jsonData){
                try{
                    JSONObject jsonObject = new JSONObject(jsonData);
                    String token = jsonObject.getString("token");
                    return token;
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            String getUserId(String jsonData){
                try{
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("user");
                    String userId = jsonObject1.getString("userId");
                    return userId;
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

        });
    }
}
