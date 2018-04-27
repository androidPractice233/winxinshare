package com.scut.weixinshare.db;

public class Comment {
    private String commentId;
    private String momentId;
    private String senderId;
    private String receiverId;
    private String createTime;
    private String content;

    //构造器参数之外的属性可以为空
    public Comment(String commentId, String momentId, String senderId, String receiverId, String createTime, String content) {
        this.commentId = commentId;
        this.momentId = momentId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createTime = createTime;
        this.content = content;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getMomentId() {
        return momentId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getContent() {
        return content;
    }
}
