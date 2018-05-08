package com.scut.weixinshare.view.component;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayout;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.utils.GlideUtils;
import com.scut.weixinshare.utils.StringUtils;

import java.util.List;

//动态信息显示封装（不包含评论）
public class MomentView extends ConstraintLayout implements View.OnClickListener {

    private ImageView portrait;                 //用户头像
    private TextView nickname;                  //用户昵称
    private TextView location;                  //动态发布地点
    private TextView time;                      //动态发布事件
    private TextView textContent;               //文本内容
    private NineGridPatternView picContent;     //图片内容
    private ImageButton addComment;             //评论按钮
    private TextView commentCount;              //评论数目显示
    //private LinearLayout comments;
    private MomentViewListener listener;

    public MomentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_moment, this);
        setOnClickListener(this);
        portrait = findViewById(R.id.portrait);
        portrait.setOnClickListener(this);
        nickname = findViewById(R.id.nickname);
        nickname.setOnClickListener(this);
        location = findViewById(R.id.location);
        time = findViewById(R.id.time);
        textContent = findViewById(R.id.content_text);
        picContent = findViewById(R.id.content_pics);
        addComment = findViewById(R.id.add_comment);
        addComment.setOnClickListener(this);
        commentCount = findViewById(R.id.comment_count);
        //comments = findViewById(R.id.comments);
    }

    public void setView(Moment moment){
        GlideUtils.loadImageViewInCircleCrop(getContext(), moment.getPortrait(), portrait);
        nickname.setText(moment.getNickName());
        location.setText(moment.getLocation());
        time.setText(StringUtils.TimeToString(moment.getCreateTime()));
        textContent.setText(moment.getTextContent());
        List<Uri> picUris = moment.getPicContent();
        if(picUris != null && picUris.size() > 0){
            picContent.setVisibility(View.VISIBLE);
            picContent.setView(picUris);
        } else {
            picContent.setVisibility(View.GONE);
        }
        commentCount.setText(String.valueOf(moment.getCommentList().size()));
        /*comments.removeAllViews();
        List<Comment> commentList = moment.getCommentList();
        if(commentList != null && commentList.size() > 0){
            comments.setVisibility(View.VISIBLE);
            for(Comment comment : commentList){
                comments.addView(initCommentView(comment));
            }
        } else {
            comments.setVisibility(View.GONE);
        }*/
    }

    public void setListener(MomentViewListener listener){
        this.listener = listener;
    }

    //添加评论计数
    //正数为增加，负数为减少
    public void addCommentCount(int i){
        int count = Integer.parseInt(commentCount.getText().toString());
        if(count + i <= 0){
            commentCount.setText("0");
        } else {
            commentCount.setText(String.valueOf(count + i));
        }
    }

    @Override
    public void onClick(View view) {
        if(listener != null){
            switch (view.getId()) {
                case R.id.portrait:
                    listener.onPortraitClick();
                    break;
                case R.id.nickname:
                    listener.onNickNameClick();
                    break;
                case R.id.add_comment:
                    listener.onAddCommentButtonClick();
                    break;
                default:
                    listener.onItemClick();
                    break;
            }
        }
    }

    /*private TextView initCommentView(Comment comment){
        TextView commentItem = new TextView(comments.getContext());
        commentItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        commentItem.setTextColor(Color.BLACK);
        commentItem.setPadding(2, 2, 2, 2);
        SpannableStringBuilder commentStr = new SpannableStringBuilder(comment.getSenderId());
        commentStr.setSpan(new ForegroundColorSpan(getResources()
                        .getColor(R.color.colorAccent)), 0,
                commentStr.length(),
                SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        String recvId = comment.getRecvId();
        if(recvId != null && !"".equals(recvId)){
            int pos = commentStr.length();
            commentStr.append("@");
            commentStr.append(recvId);
            commentStr.setSpan(new ForegroundColorSpan(getResources()
                            .getColor(R.color.colorAccent)), pos + 1,
                    commentStr.length(),
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        commentStr.append(": ");
        commentStr.append(comment.getContent());

        commentItem.setText(commentStr);
        commentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentClick();
            }
        });
        return commentItem;
    }*/

    //动态视图监听器
    public interface MomentViewListener{

        //头像点击事件监听
        void onPortraitClick();

        //昵称点击事件监听
        void onNickNameClick();

        //全视图点击事件监听
        void onItemClick();

        //评论按钮点击事件监听
        void onAddCommentButtonClick();

    }
}
