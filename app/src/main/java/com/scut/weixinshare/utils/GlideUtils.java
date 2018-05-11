package com.scut.weixinshare.utils;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.scut.weixinshare.R;

//Glide图片加载工具
public class GlideUtils {

    //普通地加载图片
    public static void loadImageView(Context context, Uri uri, ImageView imageView){
        RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_image_gray_100dp)
                .error(R.drawable.ic_broken_image_gray_100dp);
        Glide.with(context).load(uri).apply(options).into(imageView);
    }

    //加载圆形裁剪过的图片
    public static void loadImageViewInCircleCrop(Context context, Uri uri, ImageView imageView){
        RequestOptions options = new RequestOptions().circleCrop()
                .placeholder(R.drawable.ic_image_gray_100dp)
                .error(R.drawable.ic_broken_image_gray_100dp);
        Glide.with(context).asBitmap().load(uri).apply(options).into(imageView);
    }

    //暂停加载图片
    public static void pauseLoading(Context context){
        Glide.with(context).pauseRequests();
    }

    //继续加载图片
    public static void resumeLoading(Context context){
        Glide.with(context).resumeRequests();
    }

}
