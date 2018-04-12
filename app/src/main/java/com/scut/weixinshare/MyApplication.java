package com.scut.weixinshare;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.retrofit.EncryptConverterFactory;
import com.scut.weixinshare.service.KeyInitService;
import com.scut.weixinshare.utils.AES;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.UserDictionary.Words.APP_ID;

/**
 * 存放全局变量
 * 
 * @author sszvip
 * 
 */
public class MyApplication extends MultiDexApplication
		//implements AMapLocationListener {
{  private static MyApplication instance;
    public static MyApplication getInstance() {  
        return instance;  
    }  

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		try {
			NetworkManager.getInstance().exchangeKey();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	//	public static Context getAppContext(){
//		return context;
//	}

//	public   static  ILoginConfig getCurrUser() {
//		return currUser;
//	}
//	public   static  void setCurrUser(ILoginConfig iLoginConfig) {
//		currUser=iLoginConfig;
//	}

//	public static void setCurrUser(UserInfo currUser) {
//		MyApplication.currUser = currUser;
//
//		final String from = currUser.getUsername();
//		AVImClientManager.getInstance().open(from, new AVIMClientCallback() {
//			@Override
//			public void done(AVIMClient avimClient, AVIMException e) {
//				if(e == null){
//					EventBus.getDefault().post(new SigninSuccessEvent());
//				}
//			}
//		});
//	}



//	private void initDb(){
//		x.Ext.init(this);
//		x.Ext.setDebug(AppConfig.isTestMode);
//		mDaoConfig = new DbManager.DaoConfig()
//				.setDbName(IConst.DB_NAME)
//				.setDbDir(new File(IConst.PATH_ROOT))
//				.setDbVersion(IConst.DB_VER_NUM)
//				.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
//					@Override
//					public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
//						// TODO: ...
//						// db.addColumn(...);
//						// db.dropTable(...);
//						try {
//							db.dropTable(LoginConfig.class);
//							db.dropTable(UserInfo.class);
//						} catch (DbException e) {
//							e.printStackTrace();
//						}
//					}
//				});
//	}
//	private void initFiles() {
//		File dir = new File(IConst.PATH_ROOT);
//		if (!dir.exists()) {
//			dir.mkdir();
//		}
//	}
//
//
//	public DbManager getDb() {
//		if(mDb==null){
//           mDb =  x.getDb(mDaoConfig);
//		}
//		return mDb;
//	}
//
//	@Override
//	public ILoginConfig getLoginConfig() {
//		if(mLoginConfig==null){
//			try {//数据库只存储一个用户
//				List<LoginConfig> list = getDb().findAll(LoginConfig.class);
//				if(list!=null && list.size()>0){
//					mLoginConfig = list.get(0);
//				}
//			} catch (DbException e) {
//				e.printStackTrace();
//			}
//		}
//		return mLoginConfig;
//	}
//
//	@Override
//	public void saveLoginConfig(ILoginConfig login) {
//        if(login!=null){
//			try {
//				mLoginConfig = (LoginConfig)login;
//				getDb().saveOrUpdate(mLoginConfig);
//			} catch (DbException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * 初始化定位
//	 */
//	private void initLocation() {
//		// 初始化定位，只采用网络定位
//		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
//		mLocationManagerProxy.setGpsEnable(false);
//		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
//		// 在定位结束后，在合适的生命周期调用destroy()方法
//		// 其中如果间隔时间为-1，则定位只定一次,
//		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
//		mLocationManagerProxy.requestLocationData(
//				LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
//
//	}
//
//	@Override
//	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//	}
//
//	@Override
//	public void onProviderEnabled(String arg0) {
//	}
//
//	@Override
//	public void onProviderDisabled(String arg0) {
//	}
//
//	@Override
//	public void onLocationChanged(Location arg0) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void onLocationChanged(AMapLocation location) {
//
//		if (location != null && location.getAMapException().getErrorCode() == 0) {
//			// 定位成功回调信息，设置相关消息
//			String province = location.getProvince();
//			String city = location.getCity();
//			String district = location.getDistrict();// 区
//			Double lat = location.getLatitude();
//			Double log = location.getLongitude();
//
//			PrefManager.saveString(getApplicationContext(),
//					IConst.KEY_GIS_PROVINCE, province);
//			PrefManager.saveString(getApplicationContext(),
//					IConst.KEY_GIS_CITY, city);
//			PrefManager.saveString(getApplicationContext(),
//					IConst.KEY_GIS_DISTRICT, district);
//
//			PrefManager.saveFloat(getApplicationContext(),
//					IConst.KEY_GIS_LATITUDE, lat.floatValue());
//			PrefManager.saveFloat(getApplicationContext(),
//					IConst.KEY_GIS_LONGTITUDE, log.floatValue());
//		} else {
//			Log.e("AmapErr", "Location ERR:"
//					+ location.getAMapException().getErrorCode());
//		}
//
//	}
//
//	private LocationManagerProxy mLocationManagerProxy;
//
//	public  void dbquit() {
//		try {
//			getDb().delete(LoginConfig.class);
//			System.exit(0);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//		@Override
//		public void quit(boolean isClearData){
//			if(isClearData){
//			SysManager.getInstance().logout(this);
//			}
//		}


}
