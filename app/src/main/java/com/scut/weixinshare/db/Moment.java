package com.scut.weixinshare.db;

public class Moment {
    private String momentId;
    private String userId;
    private String createTime;
    private String location;
    private String pictureUrl;
    private String content;
    private String updateTime;

    //构造器参数之外的属性可以为空
    public Moment(String momentId, String userId, String createTime,String updateTime, String location) {
        this.momentId = momentId;
        this.userId = userId;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.location = location;
    }

    public Moment(String momentId, String userId, String createTime,String updateTime, String location, String pictureUrl, String content) {
        this.momentId = momentId;
        this.userId = userId;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.location = location;
        this.content = content;
        this.pictureUrl = pictureUrl;
    }

    //以下为getter和setter
    public String getMomentId() {
        return momentId;
    }
    public String getUserId() {
        return userId;
    }
    public String getCreateTime() {
        return createTime;
    }
    public String getLocation() {
        return location;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getPictureUrl() {
        return pictureUrl;
    }
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
