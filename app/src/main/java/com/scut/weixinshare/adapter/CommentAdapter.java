package com.scut.weixinshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.db.Comment;
import com.scut.weixinshare.db.DBOperator;
import com.scut.weixinshare.db.Moment;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.view.MainActivity;
import com.scut.weixinshare.view.MomentDetailActivity;
import com.tencent.wcdb.database.SQLiteDebug;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private List<Comment> comments;
    DBOperator dbOperator;

    public CommentAdapter(List<Comment> comments,DBOperator dbOperator){
        this.comments = comments;
         this.dbOperator = dbOperator;
         this.updateComments();
    }

    public void updateComments(){
        comments = dbOperator.selectCommentByUser(MyApplication.currentUser.getUserId());
        this.notifyDataSetChanged();
        if(comments.size()==0){

        }
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
        final CommentViewHolder holder = new CommentViewHolder(view);
        holder.commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position  = holder.getAdapterPosition();
                Comment comment = comments.get(position);
                Toast.makeText(v.getContext(),comment.getContent(),Toast.LENGTH_LONG).show();
                //DBOperator dbOperator = new DBOperator();
                //Moment moment = dbOperator.selectMoment(comment.getMomentId());
                //打开动态详情
                MomentDetailActivity.activityStart(v.getContext(),comment.getMomentId(),true);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        String content = "你好吗你好吗怀念士大夫啊士大夫看见啦士大夫啊圣诞节分厘卡绝对数量";

        Comment comment = comments.get(position);
        User user = dbOperator.selectUser(comment.getSenderId());
        String string;
        if(user == null){
            //本地数据库没有该用户
            string = comment.getSenderId()+"提到了你：";
        }
        else {
            string = user.getNickName()+"提到了你：";
        }
        Timestamp timestamp = new Timestamp(Long.parseLong(comment.getCreateTime()));
        //Date date = new Date(0);
        holder.timeText.setText(timestamp.toString());
        holder.timeText.setGravity(Gravity.CENTER);
        holder.senderText.setText(string);
        holder.commentText.setText(comments.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder{
        View commentView;
        TextView timeText;
        TextView commentText;
        TextView senderText;
        public CommentViewHolder(View view){
            super(view);
            commentView = view;
            timeText = view.findViewById(R.id.item_comment_time);
            commentText = view.findViewById(R.id.item_comment_text);
            senderText = view.findViewById(R.id.item_comment_sender);
        }
    }
}
