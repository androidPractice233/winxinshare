package com.scut.weixinshare.model;

/**
 * Created by skyluo on 2018/4/11.
 */

public class EncryptBean {
    private boolean success;
    private String encyptData;
    public boolean isSuccess() {
        return success;
    }

    public String getEncyptData() {
        return encyptData;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setEncyptData(String encyptData) {
        this.encyptData = encyptData;
    }
}
