package com.scut.weixinshare.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.scut.weixinshare.model.source.MomentUserData;

import java.sql.Timestamp;

//封装评论信息
public class Comment implements Parcelable {

    private String commentId;         //评论id
    private String sendId;          //发送者id
    private String recvId;            //接收者id，可能为空
    private String sendNickName;      //发送者昵称
    private Uri portrait;
    private String recvNickName;      //接收者昵称，可能为空
    private String content;           //评论内容
    private Timestamp createTime;     //发送时间

    private Comment(){
        this(null, null, null, null,
                null, null, null, null);
    }

    public Comment(String commentId, String senderId, String recvId, String sendNickName,
                   Uri portrait, String recvNickName, String content,
                   Timestamp createTime){
        this.commentId = commentId;
        this.sendId = senderId;
        this.recvId = recvId;
        this.sendNickName = sendNickName;
        this.portrait = portrait;
        this.recvNickName = recvNickName;
        this.content = content;
        this.createTime = createTime;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getRecvId() {
        return recvId;
    }

    public void setRecvId(String recvId) {
        this.recvId = recvId;
    }

    public String getSendNickName() {
        return sendNickName;
    }

    public void setSendNickName(String sendNickName) {
        this.sendNickName = sendNickName;
    }

    public Uri getPortrait() {
        return portrait;
    }

    public void setPortrait(Uri portrait) {
        this.portrait = portrait;
    }

    public String getRecvNickName() {
        return recvNickName;
    }

    public void setRecvNickName(String recvNickName) {
        this.recvNickName = recvNickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public void setSenderData(MomentUserData userData){
        this.sendNickName = userData.getNickName();
        this.portrait = userData.getPortrait();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(commentId);
        parcel.writeString(sendId);
        parcel.writeString(recvId);
        parcel.writeParcelable(portrait, i);
        parcel.writeString(sendNickName);
        parcel.writeString(recvNickName);
        parcel.writeString(content);
        parcel.writeSerializable(createTime);
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>(){

        @Override
        public Comment createFromParcel(Parcel parcel) {
            Comment comment = new Comment();
            comment.commentId = parcel.readString();
            comment.sendId = parcel.readString();
            comment.recvId = parcel.readString();
            comment.portrait = parcel.readParcelable(Uri.class.getClassLoader());
            comment.sendNickName = parcel.readString();
            comment.recvNickName = parcel.readString();
            comment.content = parcel.readString();
            comment.createTime = (Timestamp) parcel.readSerializable();
            return comment;
        }

        @Override
        public Comment[] newArray(int i) {
            return new Comment[i];
        }
    };
}
