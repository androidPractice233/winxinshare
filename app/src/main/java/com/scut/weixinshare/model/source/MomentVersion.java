package com.scut.weixinshare.model.source;

import java.sql.Date;
import java.sql.Timestamp;

public class MomentVersion{

    String momentId;
    Date updateTime;

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMomentId() {
        return momentId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
