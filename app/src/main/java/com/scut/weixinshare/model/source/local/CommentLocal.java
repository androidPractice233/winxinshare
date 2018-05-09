package com.scut.weixinshare.model.source.local;

import com.scut.weixinshare.db.Comment;

import java.sql.Timestamp;

//封装本地存储的评论信息
public class CommentLocal {
    private String commentId;
    private String senderId;
    private String receiverId;
    private Timestamp createTime;
    private String content;

    public CommentLocal(Comment comment){
        this.commentId = comment.getCommentId();
        this.senderId = comment.getSenderId();
        this.receiverId = comment.getReceiverId();
        this.createTime = Timestamp.valueOf(comment.getCreateTime());
        this.content = comment.getContent();
    }

    public String getCommentId() {
        return commentId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public String getContent() {
        return content;
    }

}
