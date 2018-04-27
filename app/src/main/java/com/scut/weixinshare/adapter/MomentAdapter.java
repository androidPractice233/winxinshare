package com.scut.weixinshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scut.weixinshare.R;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.view.component.MomentView;

import java.util.List;

//主页动态recyclerView对应adapter
public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder> {

    private List<Moment> momentList;         //动态数据列表
    private MomentItemListener listener;     //itemView监听器

    static class ViewHolder extends RecyclerView.ViewHolder{
        MomentView item;

        ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.moment);
        }
    }

    public MomentAdapter(List<Moment> momentList, MomentItemListener listener){
        this.momentList = momentList;
        this.listener = listener;
    }

    //设置动态数据
    public void setAdapterData(List<Moment> momentList){
        this.momentList = momentList;
    }

    //尾部追加动态数据
    public void addAdapterData(List<Moment> momentList){
        this.momentList.addAll(momentList);
    }

    //首部追加动态数据
    public void insertAdapterData(List<Moment> momentList){
        this.momentList.addAll(0, momentList);
    }

    //修改特定位置的动态数据
    public void updateAdapterData(Moment moment, int position){
        momentList.get(position).setCommentList(moment.getCommentList());
    }

    @Override
    public MomentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_moment, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MomentAdapter.ViewHolder holder, int position) {
        final Moment moment = momentList.get(position);
        holder.item.setView(moment);
        holder.item.setListener(new MomentView.MomentViewListener() {
            @Override
            public void onPortraitClick() {
                listener.onPortraitClick(moment, holder.getAdapterPosition());
            }

            @Override
            public void onNickNameClick() {
                listener.onNickNameClick(moment, holder.getAdapterPosition());
            }

            @Override
            public void onItemClick() {
                listener.onItemClick(moment, holder.getAdapterPosition());
            }

            @Override
            public void onAddCommentButtonClick() {
                listener.onAddCommentButtonClick(moment, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return momentList.size();
    }

    public interface MomentItemListener{

        void onPortraitClick(Moment moment, int position);

        void onNickNameClick(Moment moment, int position);

        void onItemClick(Moment moment, int position);

        void onAddCommentButtonClick(Moment moment, int position);
    }

}
