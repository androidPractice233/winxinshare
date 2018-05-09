package com.scut.weixinshare.model.source;

import android.net.Uri;

//封装动态中的用户信息
public class MomentUserData {

    private String userId;       //用户id
    private String nickName;     //用户昵称
    private Uri portrait;        //用户头像

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Uri getPortrait() {
        return portrait;
    }

    public void setPortrait(Uri portrait) {
        this.portrait = portrait;
    }

}
