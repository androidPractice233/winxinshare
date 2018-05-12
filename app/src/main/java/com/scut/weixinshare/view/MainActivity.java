package com.scut.weixinshare.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scut.weixinshare.IConst;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.contract.HomeContract;
import com.scut.weixinshare.db.Comment;
import com.scut.weixinshare.db.DBOperator;
import com.scut.weixinshare.db.Test;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.source.LocationDataSource;
import com.scut.weixinshare.presenter.PersonHomePresenter;
import com.scut.weixinshare.retrofit.BaseCallback;
import com.scut.weixinshare.service.PullCommentService;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.model.source.LocationRepository;
import com.scut.weixinshare.model.source.MomentDataSource;
import com.scut.weixinshare.model.source.MomentsRepository;
import com.scut.weixinshare.model.source.local.MomentDatabaseSource;
import com.scut.weixinshare.model.source.remote.MomentRemoteServerSource;
import com.scut.weixinshare.presenter.HomePresenter;
import com.scut.weixinshare.presenter.UserPresenter;
import com.scut.weixinshare.utils.LocationUtils;
import com.scut.weixinshare.utils.ToastUtils;
import com.scut.weixinshare.view.fragment.CommentFragment;
import com.scut.weixinshare.view.fragment.PersonHomeFragment;
import com.tencent.wcdb.database.SQLiteDebug;

import org.json.JSONArray;
import org.json.JSONObject;
import com.scut.weixinshare.view.fragment.HomeFragment;
import com.scut.weixinshare.view.fragment.MainFragment;
import com.scut.weixinshare.view.fragment.UserFragment;

import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.scut.weixinshare.R.id.swipe_refresh;
import static com.scut.weixinshare.R.id.view;

public class MainActivity extends AppCompatActivity {

    Button btnPopPhoto;
    Button button;
    Button locationBtn;
    Toolbar toolbar;
    FragmentPagerAdapter adapter;
    private List<Fragment> frag_list;// 声明一个list集合存放Fragment（数据源）
    String[] permission={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

    public static String TOKEN;
    public static String USERID;
    public int newCommentNum;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_COMMENT_NUM:
                    newCommentNum = msg.arg1;
                    //textView.setText(msg.arg1+"");
                    break;
                default:
                    break;
            }
        }
    };
    public static final int UPDATE_COMMENT_NUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_ntb);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUI();
        this.setUpdateCommentNum();


        //启动时检查权限
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            handleLocationPermi();
        }


//        button= (Button) findViewById(R.id.testButton);
//        btnPopPhoto= (Button) findViewById(R.id.btnPopPhoto);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NetworkManager.getInstance().test(new BaseCallback() {
//                    @Override
//                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
//                        ResultBean resultBean=  response.body();
//                        if(this.checkResult(MainActivity.this,resultBean)) {
//                            Toast.makeText(MainActivity.this, (String) resultBean.getData(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResultBean> call, Throwable t) {
//                        Log.e("MainActivity", t.getMessage()  );
//                    }
//                });
//            }
//        });
//
//        btnPopPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PictureSelector.create(MainActivity.this)
//                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio(
//                        .maxSelectNum(9)// 最大图片选择数量 int
//                        .imageSpanCount(4)// 每行显示个数 int
//                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
//                        .previewImage(false)// 是否可预览图片 true or false
//                        .isCamera(true)// 是否显示拍照按钮 true or false
//                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
//                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
//            }
//        });
//    }



//        locationBtn=(Button)findViewById(R.id.btn_loca);
//        final TextView text = (TextView) findViewById( R.id.text );
//        locationBtn.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Location location = LocationUtils.getInstance( MainActivity.this ).returnLocation();
//                if (location != null) {
//                    String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
//                    Log.d( "LocationUtils", address );
//                    text.setText( address );
//                }
//                else
//                    Log.d("LocationUtils", "无法获得位置类");
//            }
//        } );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
        case R.id.setting:
           UserActivity.actionStart(getApplicationContext(),MyApplication.currentUser.getUserId());
            break;
        }
            return true;
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
        //LocationUtils.getInstance(this).removeLocationUpdatesListener();

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

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ViewPager viewPager =  findViewById(R.id.vp_horizontal_ntb);
        HomeFragment homefragment = new HomeFragment();
        new HomePresenter(homefragment, MomentsRepository.getInstance(MomentDatabaseSource.getInstance(), MomentRemoteServerSource.getInstance()),
                LocationRepository.getInstance());

        CommentFragment commentFragment = new CommentFragment();


        PersonHomeFragment personHomeFragment=new PersonHomeFragment();
        new PersonHomePresenter(personHomeFragment, MomentsRepository.getInstance(MomentDatabaseSource.getInstance(), MomentRemoteServerSource.getInstance()),
                LocationRepository.getInstance(),MyApplication.currentUser.getUserId());
        MainFragment fragment2 = new MainFragment();

//        PersonHomeFragment personHomeFragment= new PersonHomeFragment();
//        new PersonHomePresenter(personHomeFragment,MomentsRepository.getInstance(MomentDatabaseSource.getInstance(), MomentRemoteServerSource.getInstance()),  LocationRepository.getInstance(),MyApplication.currentUser.getUserId());

        // 实例化对象
        frag_list = new ArrayList<Fragment>();
        frag_list.add(homefragment);
        frag_list.add(commentFragment);
        frag_list.add(personHomeFragment);


        // 设置适配器
         adapter = new FragmentPagerAdapter(
                getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return frag_list.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return frag_list.get(arg0);
            }


        };
        viewPager.setAdapter(adapter);

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icons8_home_page_50),
                        Color.parseColor(colors[0]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("周围动态")
//                        .badgeTitle("NTB")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[1]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("评论")
//                        .badgeTitle("with")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icons8_user_50),
                        Color.parseColor(colors[1]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("我的")
//                        .badgeTitle("with")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });


    }

    //登录成功后调用
    //获取评论更新，并把更新的评论数目发送到主页面
    private void setUpdateCommentNum(){
        new Thread(new Runnable() {
            //初始时间最小
            private String lastUpdateTime =null;

            @Override
            public void run() {

                //初始化时间

                //测试阶段先不设
                getLastUpdateTime();


                while(MyApplication.getInstance().getToken()==null){
                    Log.d("getUpdateComment","token is null");
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(MainActivity.this, "尚未登录");
                        }
                    });
                    //
                }

//                SharedPreferences sharedPreferences = getSharedPreferences("weixinshare", Context.MODE_PRIVATE); //私有数据
//                String s = sharedPreferences.getString("token",null);
//                if(s==null)
//                    Log.d("getUpdateComment","sharePreference token is null");
//                else
//                    Log.d("getUpdateComment",s);


                int a = 0;
                while(true) {
                    getUpdateComments();
                    updateNum(a++);
                    try {
                        Thread.sleep(300000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }

            //发送数据到MainActivity
            private void updateNum(int num){
                Message message = new Message();
                message.what = UPDATE_COMMENT_NUM;
                message.arg1 = num;
                handler.sendMessage(message);
            }
            //获取更新动态
            void getUpdateComments(){
                NetworkManager.getInstance().pullComment(new Callback<ResultBean>() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                        ResultBean resultBean = response.body();
                        if(resultBean==null){
                            Log.d("getUpdateComment","resultBean is null");
                            return;
                        }
                        Log.d("getUpdateComment","res success");
                        Object data = resultBean.getData();
                        Log.d("getUpdateComment",resultBean.getCode()+"");



                        //如果返回结果为空，不用更新lastUpdateTime
                        if(data==null){
                            Log.d("getUpdateComment","no comment response");
                            return;
                        }
                        else{
                            if(resultBean.getCode()==200){
                                String dataString = resultBean.getData().toString();
                                Log.d("getUpdateComment",dataString);
//                                processData(dataString);
                                getLastUpdateTime();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {
                        Log.d("loginRes",t.getMessage());

                    }
                    //处理返回数据
                    private void processData(String jsonData){
                        try{
                            JSONArray jsonArray = new JSONArray(jsonData);
                            int n = jsonArray.length();
//                            DBOperator dbOperator = new DBOperator();
//                            for(int i=0;i<n;i++){
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                String commentId = jsonObject.getString("commentId");
//                                String momentId = jsonObject.getString("momentId");
//                                String sendId = jsonObject.getString("sendId");
//                                String recvId = jsonObject.getString("recvId");
//                                String content = jsonObject.getString("content");
//
//                                long createTime = jsonObject.getLong("createTime");
//
//                                dbOperator.insertComment
//                                        (new Comment(commentId,momentId,sendId,recvId,createTimeStr,content));
//                            }
//                            dbOperator.close();
                            if(n!=0) {
                                Toast.makeText(MainActivity.this, "Hay!你有" + n + "条新评论", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                },lastUpdateTime);
            }
            //获取最近更新时间
            private void getLastUpdateTime(){
                DBOperator dbOperator = new DBOperator();
                String time = dbOperator.getLastTime();
                dbOperator.close();
                if(time!=null){
                    lastUpdateTime = time;
                }
            }

        }).start();
    }
}

