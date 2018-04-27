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

    private UUID momentId;                      //动态id
    private String username;                    //用户账号
    private String nickname;                    //用户昵称
    private transient Uri portrait;             //用户头像
    private Timestamp createTime;               //动态创建时间
    private String location;                    //动态位置名
    private String textContent;                 //动态文字内容
    private transient List<Uri> picContent;     //动态图片内容
    private List<Comment> commentList;          //评论
    private Timestamp updateTime;               //动态更新时间

    public Moment(){
        this(null, null, null, null, null,
                null, null, null, null,
                null);
    }

    public Moment(UUID momentId, String username, String nickname, Uri portrait,
                  Timestamp createTime, String location, String textContent, List<Uri> picContent,
                  List<Comment> commentList, Timestamp updateTime){
        this.momentId = momentId;
        this.username = username;
        this.nickname = nickname;
        this.portrait = portrait;
        this.createTime = createTime;
        this.location = location;
        this.textContent = textContent;
        this.picContent = picContent;
        this.commentList = commentList;
        this.updateTime = updateTime;
    }

    public UUID getMomentId() {
        return momentId;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public Uri getPortrait() {
        return portrait;
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
        parcel.writeSerializable(momentId);
        parcel.writeString(username);
        parcel.writeString(nickname);
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
            moment.momentId = (UUID) parcel.readSerializable();
            moment.username = parcel.readString();
            moment.nickname = parcel.readString();
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
