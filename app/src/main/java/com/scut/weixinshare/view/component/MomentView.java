package com.scut.weixinshare.view.component;

import android.content.Context;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.scut.weixinshare.R;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.utils.GlideUtils;
import com.scut.weixinshare.utils.MomentUtils;

import java.util.List;

//动态信息显示封装（不包含评论）
public class MomentView extends ConstraintLayout implements View.OnClickListener {

    private ImageView portrait;                 //用户头像
    private TextView nickname;                  //用户昵称
    private TextView location;                  //动态发布地点
    private TextView time;                      //动态发布事件
    private TextView textContent;               //文本内容
    private NineGridPatternView picContent;     //图片内容
    private TextView commentCount;              //评论数目显示
    private MomentViewListener listener;
    private Moment moment;

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
        picContent.setListener(new NineGridPatternView.NineGridPatternViewListener() {
            @Override
            public void onItemClick(List<Uri> uriList, int position) {
                if(listener != null){
                    listener.onImagesClick(uriList, position);
                }
            }
        });
        ImageButton addComment = findViewById(R.id.add_comment);
        addComment.setOnClickListener(this);
        commentCount = findViewById(R.id.comment_count);
    }

    public void setView(Moment moment){
        this.moment = moment;
        if(moment.getPortrait() != null) {
            GlideUtils.loadImageViewInCircleCrop(getContext(), moment.getPortrait(), portrait);
        }
        nickname.setText(moment.getNickName());
        location.setText(moment.getLocation());
        time.setText(MomentUtils.TimeToString(moment.getCreateTime()));
        textContent.setText(moment.getTextContent());
        List<Uri> picUris = moment.getPicContent();
        if(picUris != null && picUris.size() > 0){
            picContent.setVisibility(View.VISIBLE);
            picContent.setView(picUris);
        } else {
            picContent.setVisibility(View.GONE);
        }
        if(moment.getCommentList() != null) {
            commentCount.setText(String.valueOf(moment.getCommentList().size()));
        } else {
            commentCount.setText("0");
        }
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
                    listener.onPortraitClick(moment);
                    break;
                case R.id.nickname:
                    listener.onNickNameClick(moment);
                    break;
                case R.id.add_comment:
                    listener.onAddCommentButtonClick(moment);
                    break;
                default:
                    listener.onItemClick(moment);
                    break;
            }
        }
    }

    //动态视图监听器
    public interface MomentViewListener {

        //头像点击事件监听
        void onPortraitClick(Moment moment);

        //昵称点击事件监听
        void onNickNameClick(Moment moment);

        //全视图点击事件监听
        void onItemClick(Moment moment);

        //评论按钮点击事件监听
        void onAddCommentButtonClick(Moment moment);

        //动态图片点击事件监听
        void onImagesClick(List<Uri> images, int position);

    }
}
