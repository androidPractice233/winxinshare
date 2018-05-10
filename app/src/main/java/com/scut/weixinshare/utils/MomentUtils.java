package com.scut.weixinshare.utils;

import android.net.Uri;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.local.CommentLocal;
import com.scut.weixinshare.model.source.local.MomentLocal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MomentUtils {

    private static long min = 60000;
    private static long hour = min * 60;
    private static long day = hour * 24;

    public static Moment momentLocalToMoment(MomentLocal momentLocal){
        Moment moment = new Moment(momentLocal.getMomentId(), momentLocal.getUserId(), null, null,
                momentLocal.getCreateTime(), momentLocal.getLocation(), momentLocal.getContext(),
                momentLocal.getPicContent(), null, momentLocal.getUpdateTime());
        List<Comment> commentList = new ArrayList<>();
        for(CommentLocal commentLocal : momentLocal.getCommentList()){
            commentList.add(new Comment(commentLocal.getCommentId(), commentLocal.getSenderId(),
                    commentLocal.getReceiverId(), null, null,
                    null, commentLocal.getContent(), commentLocal.getCreateTime()));
        }
        moment.setCommentList(commentList);
        return moment;
    }

    public static List<Uri> imageUriStringToList(String uris){
        if(uris == null){
            return null;
        } else {
            String[] uriArray = uris.split(",");
            List<Uri> uriList = new ArrayList<>();
            for (String uri : uriArray) {
                uriList.add(Uri.parse(IConst.URL_BASE + uri.substring(1)));
            }
            return uriList;
        }
    }

    public static String imageUriListToString(List<Uri> uriList){
        if(uriList == null && uriList.size() == 0){
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            boolean isFirst = true;
            for (Uri uri : uriList) {
                if (isFirst) {
                    isFirst = false;
                    stringBuilder.append(uri.toString().substring(IConst.URL_BASE.length() - 1));
                } else {
                    stringBuilder.append(",");
                    stringBuilder.append(uri.toString().substring(IConst.URL_BASE.length() - 1));
                }
            }
            return stringBuilder.toString();
        }
    }

    public static String TimeToString(Timestamp timestamp){
        long before = timestamp.getTime();
        long now = new Date().getTime();
        System.out.println("before" + before);
        System.out.println("now" + now);
        long diffDay = now / day - before / day;
        if(diffDay > 0) {
            return "" + diffDay + "天前";
        }
        long diffHour = now / hour - before / hour;
        if(diffHour > 0) {
            return "" + diffHour + "小时前";
        }
        long diffMin = now / min - before / min;
        if(diffMin > 0) {
            return "" + diffMin + "分钟前";
        }
        return "" + 1 + "分钟前";
    }

}
