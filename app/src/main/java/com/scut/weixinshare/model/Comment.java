package com.scut.weixinshare.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

//封装评论信息
public class Comment implements Parcelable {

    private String commentId;           //评论id
    private String senderId;          //发送者昵称
    private String recvId;            //接收者昵称，可能为空
    private String content;           //评论内容
    private Timestamp createTime;     //发送时间

    private Comment(){
        this(null, null, null, null, null);
    }

    public Comment(String commentId, String senderId, String recvId, String content,
                   Timestamp createTime){
        this.commentId = commentId;
        this.senderId = senderId;
        this.recvId = recvId;
        this.content = content;
        this.createTime = createTime;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecvId() {
        return recvId;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(commentId);
        parcel.writeString(senderId);
        parcel.writeString(recvId);
        parcel.writeString(content);
        parcel.writeSerializable(createTime);
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>(){

        @Override
        public Comment createFromParcel(Parcel parcel) {
            Comment comment = new Comment();
            comment.commentId = parcel.readString();
            comment.senderId = parcel.readString();
            comment.recvId = parcel.readString();
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
