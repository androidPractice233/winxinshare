package com.scut.weixinshare.utils;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

//Glide图片加载工具
public class GlideUtils {

    //普通地加载图片
    public static void loadImageView(Context context, Uri uri, ImageView imageView){
        Glide.with(context).load(uri).into(imageView);
    }

    //加载圆形裁剪过的图片
    public static void loadImageViewInCircleCrop(Context context, Uri uri, ImageView imageView){
        RequestOptions options = new RequestOptions().circleCrop();
        Glide.with(context).asBitmap().load(uri).apply(options).into(imageView);
    }
}
