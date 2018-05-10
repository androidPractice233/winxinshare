package com.scut.weixinshare.view.component;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scut.weixinshare.R;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.utils.GlideUtils;

public class CommentView extends ConstraintLayout implements View.OnClickListener {

    private ImageView portrait;
    private TextView nickname;
    private TextView text;
    private CommentViewListener listener;
    private Comment comment;

    public CommentView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_comment, this);
        portrait = findViewById(R.id.comment_portrait);
        nickname = findViewById(R.id.comment_nickname);
        text = findViewById(R.id.comment_text);
        setOnClickListener(this);
    }

    public void setListener(CommentViewListener listener){
        this.listener = listener;
    }

    public void setView(Comment comment){
        this.comment = comment;
        if(comment.getPortrait() != null) {
            GlideUtils.loadImageViewInCircleCrop(getContext(), comment.getPortrait(), portrait);
        }
        nickname.setText(comment.getSendNickName());
        SpannableStringBuilder commentText = new SpannableStringBuilder();
        if(comment.getRecvId() != null){
            commentText.append("@");
            commentText.append(comment.getRecvNickName());
            commentText.append(" ");
            commentText.setSpan(new ForegroundColorSpan(getResources()
                            .getColor(R.color.colorAccent)), 0,
                    commentText.length() - 1,
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        commentText.append(comment.getContent());
        text.setText(commentText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                listener.onItemClick(comment);
                break;
        }
    }

    public interface CommentViewListener{

        void onItemClick(Comment comment);
    }
}
