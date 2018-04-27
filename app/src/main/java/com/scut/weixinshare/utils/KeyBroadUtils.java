package com.scut.weixinshare.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

//软键盘管理工具
public class KeyBroadUtils {

    //弹出软键盘（同时将为传入的view获取焦点）
    public static void showKeyBroad(View view){
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(manager != null){
            view.requestFocus();
            manager.showSoftInput(view, 0);
        }
    }

    //隐藏软键盘（同时将传入的view清除焦点）
    public static void hideKeyBroad(View view){
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(manager != null){
            view.clearFocus();
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
