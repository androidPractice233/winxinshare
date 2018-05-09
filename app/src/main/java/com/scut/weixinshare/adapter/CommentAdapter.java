package com.scut.weixinshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.scut.weixinshare.R;
import com.scut.weixinshare.db.Comment;
import com.scut.weixinshare.db.DBOperator;
import com.scut.weixinshare.db.Moment;
import com.scut.weixinshare.model.User;
import com.scut.weixinshare.view.MomentDetailActivity;
import com.tencent.wcdb.database.SQLiteDebug;

import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private List<Comment> comments;
    DBOperator dbOperator;

    public CommentAdapter(List<Comment> comments,DBOperator dbOperator){
        this.comments = comments;
         this.dbOperator = dbOperator;
    }

    public void updateComments(){
        comments = dbOperator.selectCommentByUser("0001");
        this.notifyDataSetChanged();
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
                //MomentDetailActivity.activityStart(v.getContext(),moment,true);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        User user = dbOperator.selectUser(comment.getSenderId());
        String string = user.getNickName()+" 回复你： "+comments.get(position).getContent();
        holder.text.setText(string);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder{
        View commentView;
        TextView text;
        public CommentViewHolder(View view){
            super(view);
            commentView = view;
            text = view.findViewById(R.id.item_comment_text);
        }
    }
}
