package com.scut.weixinshare.model.source;

import android.net.Uri;

public class MomentUserData {

    private String nickName;
    private Uri portrait;

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
