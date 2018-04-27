package com.scut.weixinshare.utils;

import android.content.Context;
import android.util.TypedValue;

import java.lang.reflect.TypeVariable;

//dp与px转换工具
public class DensityUtils {

    public static int dipToPx(Context context, int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static int pxToDip(Context context, int px){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px,
                context.getResources().getDisplayMetrics());
    }

}
