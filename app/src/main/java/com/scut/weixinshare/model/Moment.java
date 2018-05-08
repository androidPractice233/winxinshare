package com.scut.weixinshare.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

//动态信息封装
public class Moment implements Parcelable {

    private String momentId;               //动态id
    private String userId;                 //用户id
    private String nickName;               //用户昵称
    private Uri portrait;                  //用户头像
    private Timestamp createTime;          //动态创建时间
    private String location;               //动态位置名
    private String textContent;            //动态文字内容
    private List<Uri> picContent;          //动态图片内容
    private List<Comment> commentList;     //评论
    private Timestamp updateTime;          //动态更新时间

    private Moment(){
        this(null, null, null, null,
                null, null, null, null,
                null, null);
    }

    public Moment(String momentId, String userId, Timestamp createTime, String location,
                  String textContent, Timestamp updateTime){
        this(momentId, userId, null, null, createTime,
                location, textContent, null, null,
                updateTime);
    }

    public Moment(String momentId, String userId, Timestamp createTime, String location,
                  String textContent, List<Uri> picContent, Timestamp updateTime){
        this(momentId, userId, null, null, createTime,
                location, textContent, picContent, null,
                updateTime);
    }

    public Moment(String momentId, String userId, String nickname, Uri portrait,
                  Timestamp createTime, String location, String textContent, List<Uri> picContent,
                  List<Comment> commentList, Timestamp updateTime){
        this.momentId = momentId;
        this.userId = userId;
        this.nickName = nickname;
        this.portrait = portrait;
        this.createTime = createTime;
        this.location = location;
        this.textContent = textContent;
        this.picContent = picContent;
        this.commentList = commentList;
        this.updateTime = updateTime;
    }

    public String getMomentId() {
        return momentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }

    public Uri getPortrait() {
        return portrait;
    }

    public void setUserData(String nickName, Uri portrait){
        this.nickName = nickName;
        this.portrait = portrait;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public String getLocation() {
        return location;
    }

    public String getTextContent() {
        return textContent;
    }

    public List<Uri> getPicContent() {
        return picContent;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(momentId);
        parcel.writeString(userId);
        parcel.writeString(nickName);
        parcel.writeParcelable(portrait, i);
        parcel.writeSerializable(createTime);
        parcel.writeString(location);
        parcel.writeString(textContent);
        parcel.writeTypedList(picContent);
        parcel.writeTypedList(commentList);
        parcel.writeSerializable(updateTime);
    }

    public static final Parcelable.Creator<Moment> CREATOR = new Parcelable.Creator<Moment>(){

        @Override
        public Moment createFromParcel(Parcel parcel) {
            Moment moment = new Moment();
            moment.momentId = parcel.readString();
            moment.userId = parcel.readString();
            moment.nickName = parcel.readString();
            moment.portrait = parcel.readParcelable(Uri.class.getClassLoader());
            moment.createTime = (Timestamp) parcel.readSerializable();
            moment.location = parcel.readString();
            moment.textContent = parcel.readString();
            moment.picContent = new ArrayList<>();
            parcel.readTypedList(moment.picContent, Uri.CREATOR);
            moment.commentList = new ArrayList<>();
            parcel.readTypedList(moment.commentList, Comment.CREATOR);
            moment.updateTime = (Timestamp) parcel.readSerializable();
            return moment;
        }

        @Override
        public Moment[] newArray(int i) {
            return new Moment[i];
        }
    };

}
