package com.scut.weixinshare;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button button;
    Button locationBtn;

    String[] permission={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //启动时检查权限
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            handleLocationPermi();
        }


        button= (Button) findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkManager.getInstance().test(new Callback<ResultBean>() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                        ResultBean resultBean=  response.body();
                        Toast.makeText(MainActivity.this,(String)resultBean.getData(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {
                        Log.e("MainActivity", t.getMessage()  );
                    }
                });
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

    //申请获取权限后回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case IConst.REQUEST_LOCATION:{
                //允许获取地理位置权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                else {
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle("还可以手动开启权限").setMessage("可以前往设置->app->myapp->permission打开").setPositiveButton("确定!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();

                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationUtils.getInstance(this).removeLocationUpdatesListener();

    }


    private void handleLocationPermi(){
        //当用户拒绝掉权限时.
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                permission[0])||ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                permission[1])) {

            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("需要开启定位权限才能正常使用").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            permission,
                            IConst.REQUEST_LOCATION);
                }
            }).setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, permission, IConst.REQUEST_LOCATION);


        }
    }
}
