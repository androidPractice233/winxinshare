package com.scut.weixinshare.view.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.scut.weixinshare.view.component.MomentView;

import java.util.List;

public class MomentDetailFragment extends Fragment implements MomentDetailContract.View,
        MomentView.MomentViewListener {

    private static final String DEFAULT_HINT = "添加评论";     //评论输入框默认提示

    private MomentDetailContract.Presenter presenter;
    private MomentView momentView;
    private LinearLayout comments;
    private EditText inputComment;
    //private ImageButton releaseComment;
    private SwipeRefreshLayout swipeRefresh;
    //private Moment moment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                    String hint = inputComment.getHint().toString();
                    if(!hint.equals(DEFAULT_HINT)){
                        presenter.releaseComment(text, hint.substring(1));
                    } else {
                        presenter.releaseComment(text, null);
                    }
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
                swipeRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    private TextView initCommentView(final Comment comment){
        TextView commentItem = (TextView) getLayoutInflater().inflate(R.layout.view_comment,
                comments);
        commentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputComment.setHint("@" + comment.getSenderId());
                KeyBroadUtils.showKeyBroad(inputComment);
            }
        });
        //发送者昵称，接收者昵称，文本内容采用不同的文本格式
        SpannableStringBuilder commentStr = new SpannableStringBuilder(comment.getSenderId());
        //发送者昵称格式
        commentStr.setSpan(new StyleSpan(Typeface.BOLD), 0,
                commentStr.length(),
                SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        String receiverId = comment.getRecvId();
        if(receiverId != null && !"".equals(receiverId)){
            //接收者昵称格式
            int pos = commentStr.length();
            commentStr.append(" @");
            commentStr.append(receiverId);
            commentStr.setSpan(new ForegroundColorSpan(getResources()
                            .getColor(R.color.colorAccent)), pos + 1,
                    commentStr.length(),
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        //文本内容采用TextView的默认格式
        commentStr.append(" ");
        commentStr.append(comment.getContent());
        commentItem.setText(commentStr);
        return commentItem;
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
    public void addComment(Comment comment) {
        //向评论布局顶部添加评论
        comments.addView(initCommentView(comment), 0);
        //增加评论计数
        momentView.addCommentCount(1);
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
    public void setPresenter(MomentDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPortraitClick() {
        //点击头像事件处理
    }

    @Override
    public void onNickNameClick() {
        //点击昵称事件处理
    }

    @Override
    public void onItemClick() {
        //不做处理
    }

    @Override
    public void onAddCommentButtonClick() {
        //点击评论，弹出软键盘并设置评论输入框提示为默认样式
        inputComment.setHint(DEFAULT_HINT);
        KeyBroadUtils.showKeyBroad(inputComment);
    }

}

