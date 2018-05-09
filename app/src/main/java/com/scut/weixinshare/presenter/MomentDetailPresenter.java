package com.scut.weixinshare.presenter;

import android.content.Intent;
import android.net.Uri;

import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.contract.MomentDetailContract;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.MomentDataSource;
import com.scut.weixinshare.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MomentDetailPresenter implements MomentDetailContract.Presenter {

    private MomentDetailContract.View view;
    private boolean isDataChanged = false;
    private String momentId;
    private String receiverId;
    private MomentDataSource momentDataSource;

    public MomentDetailPresenter(MomentDetailContract.View view, String momentId,
                                 boolean isToComment, MomentDataSource momentDataSource){
        this.view = view;
        this.momentDataSource = momentDataSource;
        view.setPresenter(this);
        if(isToComment) {
            view.showKeyBroad();
        }
        this.momentId = momentId;
    }

    @Override
    public void releaseComment(String text) {
        /*Comment comment = new Comment(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                null, "傻强", Uri.parse("https://img3.duitang.com/uploads/item/201604/01/20160401215443_tYJne.jpeg"),
                null, text, new Timestamp(System.currentTimeMillis()));*/
        //向服务器上传评论
        momentDataSource.createComment(text, momentId, MyApplication.getInstance().getUserId(),
                receiverId, new MomentDataSource.CreateCommentCallback() {
            @Override
            public void onSuccess() {
                //moment.getCommentList().add(comment);
                view.showReminderMessage("评论已发送");
                view.showRefreshing();
                refreshMomentDetail();
            }

            @Override
            public void onFailure(String error) {
                if(NetworkUtils.isLoginFailed(error)){
                    view.showReminderMessage("登录失效，请重新登录");
                    view.showLoginUI();
                } else {
                    view.showReminderMessage("评论发送失败，" + error);
                }
            }
        });
        //上传成功
        /*isDataChanged = true;
        moment.getCommentList().add(comment);
        view.showReminderMessage("评论发送成功");
        view.showRefreshing();
        view.setView(moment);
        view.hideRefreshing();*/
    }

    @Override
    public Intent returnData() {
        Intent intent = new Intent();
        intent.putExtra("isChanged", isDataChanged);
        return intent;
    }

    @Override
    public void refreshMomentDetail() {
        momentDataSource.refreshMoment(momentId);
        momentDataSource.getMoment(momentId, new MomentDataSource.GetMomentCallback() {
            @Override
            public void onMomentLoaded(Moment moment) {
                isDataChanged = true;
                view.setView(moment);
                view.showReminderMessage("动态已更新");
                view.hideRefreshing();
            }

            @Override
            public void onDataNotAvailable(String error) {
                if(NetworkUtils.isLoginFailed(error)){
                    view.showReminderMessage("登录失效，请重新登录");
                    view.showLoginUI();
                } else {
                    view.showReminderMessage("动态更新失败，" + error);
                    view.hideRefreshing();
                }
            }
        });

    }

    @Override
    public void openUserData(Moment moment) {
        view.showUserDataUI(moment.getUserId());
    }

    @Override
    public void changeCommentUser(Comment comment) {
        if(comment != null) {
            this.receiverId = comment.getSendId();
            view.updateCommentUI(comment.getSendNickName());
        } else {
            this.receiverId = null;
            view.updateCommentUI(null);
        }
    }

    @Override
    public void openBigImage(List<Uri> uriList) {
        view.showBigPicUI(new ArrayList<>(uriList));
    }

    @Override
    public void start() {
        view.showRefreshing();
        momentDataSource.getMoment(momentId, new MomentDataSource.GetMomentCallback() {
            @Override
            public void onMomentLoaded(Moment moment) {
                view.setView(moment);
                view.hideRefreshing();
            }

            @Override
            public void onDataNotAvailable(String error) {
                if(NetworkUtils.isLoginFailed(error)){
                    view.showReminderMessage("登录失效，请重新登录");
                    view.showLoginUI();
                } else {
                    view.showReminderMessage("动态加载失败，" + error);
                    view.hideRefreshing();
                }
            }
        });
    }
}
