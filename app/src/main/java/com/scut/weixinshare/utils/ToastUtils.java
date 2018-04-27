package com.scut.weixinshare.utils;

import android.content.Context;
import android.widget.Toast;

//Toast发布工具，防止多条Toast发布导致Toast长时间停留在页面上
public class ToastUtils {

    private static Toast toast = null;

    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }
}
