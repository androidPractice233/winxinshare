package com.scut.weixinshare.model.source.local;

import android.net.Uri;

import com.scut.weixinshare.db.Moment;
import com.scut.weixinshare.model.source.local.CommentLocal;
import com.scut.weixinshare.utils.StringUtils;

import java.sql.Timestamp;
import java.util.List;

//封装本地存储的动态信息
public class MomentLocal {
    private String momentId;
    private String userId;
    private Timestamp createTime;
    private String location;
    private String text;
    private List<Uri> picContent;
    private List<CommentLocal> commentList;
    private Timestamp updateTime;

    public MomentLocal(Moment moment){
        this.momentId = moment.getMomentId();
        this.userId = moment.getUserId();
        this.createTime = Timestamp.valueOf(moment.getCreateTime());
        this.location = moment.getLocation();
        this.text = moment.getContent();
        this.picContent = StringUtils.imageUriStringToList(moment.getPictureUrl());
        this.updateTime = Timestamp.valueOf(moment.getUpdateTime());
    }

    public void setCommentList(List<CommentLocal> commentList){
        this.commentList = commentList;
    }

    public String getMomentId() {
        return momentId;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public String getLocation() {
        return location;
    }

    public String getContext(){
        return text;
    }

    public List<Uri> getPicContent() {
        return picContent;
    }

    public List<CommentLocal> getCommentList() {
        return commentList;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

}
