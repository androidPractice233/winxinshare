package com.scut.weixinshare.model.source;

import java.sql.Timestamp;

public class MomentVersion{

    String momentId;
    Timestamp updateTime;

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getMomentId() {
        return momentId;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }
}
