package com.scut.weixinshare.view.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scut.weixinshare.R;
import com.scut.weixinshare.contract.MomentDetailContract;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.utils.KeyBroadUtils;
import com.scut.weixinshare.utils.ToastUtils;
import com.scut.weixinshare.view.BigPicActivity;
import com.scut.weixinshare.view.UserActivity;
import com.scut.weixinshare.view.LoginActivity;
import com.scut.weixinshare.view.component.CommentView;
import com.scut.weixinshare.view.component.MomentView;

import java.util.ArrayList;
import java.util.List;

public class MomentDetailFragment extends Fragment implements MomentDetailContract.View,
        MomentView.MomentViewListener, CommentView.CommentViewListener {

    private static final String DEFAULT_HINT = "添加评论";     //评论输入框默认提示

    private MomentDetailContract.Presenter presenter;
    private MomentView momentView;
    private LinearLayout comments;
    private EditText inputComment;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moment_detail, container,
                false);
        momentView = view.findViewById(R.id.moment);
        comments = view.findViewById(R.id.comments);
        inputComment = view.findViewById(R.id.input_area_comment);
        inputComment.setHint(DEFAULT_HINT);
        ImageButton releaseComment = view.findViewById(R.id.release_comment);
        releaseComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = inputComment.getText().toString();
                if(!"".equals(text)){
                    //从输入框提示中获取接收者信息
                    presenter.releaseComment(text);
                    inputComment.setText("");
                    KeyBroadUtils.hideKeyBroad(inputComment);
                } else {
                    ToastUtils.showToast(getContext(), "输入内容不能为空");
                }
            }
        });
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refreshMomentDetail();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    private CommentView initCommentView(Comment comment){
        CommentView view = new CommentView(getContext());
        view.setListener(this);
        view.setClickable(true);
        view.setView(comment);
        return view;
    }

    @Override
    public void setView(Moment moment){
        momentView.setView(moment);
        momentView.setListener(this);
        comments.removeAllViews();
        List<Comment> commentList = moment.getCommentList();
        if(commentList != null && commentList.size() > 0){
            comments.setVisibility(View.VISIBLE);
            for(Comment comment : commentList){
                comments.addView(initCommentView(comment));
            }
        } else {
            comments.setVisibility(View.GONE);
        }
    }

    @Override
    public void showKeyBroad() {
        KeyBroadUtils.showKeyBroad(inputComment);
    }

    @Override
    public void showRefreshing() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideRefreshing() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showReminderMessage(String message) {
        ToastUtils.showToast(getContext(), message);
    }

    @Override
    public void showUserDataUI(String userId) {
        Intent intent=new Intent(getContext(),UserActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    @Override
    public void updateCommentUI(String nickname) {
        if(nickname != null) {
            inputComment.setHint("@" + nickname);
        } else {
            inputComment.setHint(DEFAULT_HINT);
        }
        KeyBroadUtils.showKeyBroad(inputComment);
    }

    @Override
    public void showLoginUI() {
       LoginActivity.relogin(getActivity());
    }

    @Override
    public void showBigPicUI(ArrayList<Uri> images) {
        BigPicActivity.activityStart(getContext(), images);
    }

    @Override
    public void setPresenter(MomentDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPortraitClick(Moment moment) {
        presenter.openUserData(moment);
    }

    @Override
    public void onNickNameClick(Moment moment) {
        presenter.openUserData(moment);
    }

    @Override
    public void onItemClick(Moment moment) {
        //不做处理
    }

    @Override
    public void onAddCommentButtonClick(Moment moment) {
        presenter.changeCommentUser(null);
    }

    @Override
    public void onImagesClick(List<Uri> images) {
        presenter.openBigImage(images);
    }

    @Override
    public void onItemClick(Comment comment) {
        presenter.changeCommentUser(comment);
    }
}

