package com.scut.weixinshare;

//Presenter基础接口
public interface BasePresenter {

    //开始执行presenter内部逻辑，一般在fragment的onResume()方法中调用
    void start();

}
