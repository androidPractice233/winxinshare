package com.scut.weixinshare.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.model.source.LocationRepository;
import com.scut.weixinshare.model.source.MomentsRepository;
import com.scut.weixinshare.model.source.local.MomentDatabaseSource;
import com.scut.weixinshare.model.source.remote.MomentRemoteServerSource;
import com.scut.weixinshare.presenter.HomePresenter;
import com.scut.weixinshare.presenter.UserPresenter;
import com.scut.weixinshare.utils.LocationUtils;
import com.scut.weixinshare.view.fragment.HomeFragment;
import com.scut.weixinshare.view.fragment.MainFragment;
import com.scut.weixinshare.view.fragment.UserFragment;
import com.scut.weixinshare.view.fragment.UserInputFragment;

import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity implements UserInputFragment.FragmentInteraction {

    Button btnPopPhoto;
    Button button;
    Button locationBtn;
    FragmentPagerAdapter adapter;
    private List<Fragment> frag_list;// 声明一个list集合存放Fragment（数据源）
    String[] permission={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_ntb);
        initUI();

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
    public void changeTitle(String title) {
        setTitle(title);
    }

    @Override
    public void updateUser(User user) {
        UserFragment fragment=(UserFragment)adapter.getItem(1);
        fragment.showUserInfo(user);
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

    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        HomeFragment homefragment = new HomeFragment();
        new HomePresenter(homefragment, MomentsRepository.getInstance(MomentDatabaseSource.getInstance(), MomentRemoteServerSource.getInstance()),
                LocationRepository.getInstance());


        UserFragment userFragment = UserFragment.newInstance("n");

        if (MyApplication.user!=null)
            new UserPresenter(userFragment, MyApplication.user);
        else {
            User user = new User("", "", "", 0, "", "", "");
            new UserPresenter(userFragment, user);
        }

        MainFragment fragment2 = new MainFragment();
        // 实例化对象
        frag_list = new ArrayList<Fragment>();
        frag_list.add(homefragment);
        frag_list.add(userFragment);


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
}

