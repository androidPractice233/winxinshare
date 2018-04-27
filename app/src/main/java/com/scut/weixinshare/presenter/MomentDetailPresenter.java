package com.scut.weixinshare.presenter;

import android.os.Bundle;

import com.scut.weixinshare.contract.MomentDetailContract;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Moment;

import java.sql.Timestamp;
import java.util.UUID;

//
public class MomentDetailPresenter implements MomentDetailContract.Presenter {

    private MomentDetailContract.View view;
    private boolean isDataChanged = false;
    private Moment moment;

    public MomentDetailPresenter(MomentDetailContract.View view, Moment moment,
                                 boolean isToComment){
        this.view = view;
        view.setPresenter(this);
        view.setView(moment);
        if(isToComment) {
            view.showKeyBroad();
        }
        this.moment = moment;
    }

    @Override
    public void releaseComment(String text, String receiverId) {
        Comment comment = new Comment(UUID.randomUUID(), "大神",
                receiverId, text, new Timestamp(System.currentTimeMillis()));
        //向服务器上传评论

        //上传成功
        isDataChanged = true;
        moment.getCommentList().add(comment);
        view.addComment(comment);
        view.showReminderMessage("评论发送成功");
    }

    @Override
    public Bundle resultToHome() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isChanged", isDataChanged);
        //动态数据有更新，将更新后的数据回传
        if(isDataChanged){
            //bundle.putSerializable("moment", moment);
            bundle.putParcelable("moment", moment);
        }
        return bundle;
    }

    @Override
    public void requestMomentDetail() {

    }

    @Override
    public void start() {

    }
}
