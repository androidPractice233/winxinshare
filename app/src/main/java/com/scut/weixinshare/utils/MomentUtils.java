package com.scut.weixinshare.utils;

import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.local.CommentLocal;
import com.scut.weixinshare.model.source.local.MomentLocal;

import java.util.ArrayList;
import java.util.List;

public class MomentUtils {

    public static Moment momentLocalToMoment(MomentLocal momentLocal){
        Moment moment = new Moment(momentLocal.getMomentId(), momentLocal.getUserId(),
                momentLocal.getCreateTime(), momentLocal.getLocation(), momentLocal.getContext(),
                momentLocal.getPicContent(), momentLocal.getUpdateTime());
        List<Comment> commentList = new ArrayList<>();
        for(CommentLocal commentLocal : momentLocal.getCommentList()){
            commentList.add(new Comment(commentLocal.getCommentId(), commentLocal.getSenderId(),
                    commentLocal.getReceiverId(), null, null,
                    null, commentLocal.getContent(), commentLocal.getCreateTime()));
        }
        moment.setCommentList(commentList);
        return moment;
    }

}
