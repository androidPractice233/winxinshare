package com.scut.weixinshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scut.weixinshare.R;

//通过添加底部footView，为recyclerView添加上拉加载效果
//recyclerView的setAdapter()方法传入本adapter实例
//数据adapter更新数据后必须使用本adapter实例的notify系列方法通知recyclerView更新
public class PullUpRefreshAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //区分view类型
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOT_VIEW = 1;

    private RecyclerView.Adapter dataAdapter;    //recyclerView数据adapter
    private ViewHolder viewHolder;               //ootItem的viewHolder
    private boolean isLoading = true;            //设定FootView是否为加载中状态

    static class ViewHolder extends RecyclerView.ViewHolder{

        ProgressBar progressBar;
        TextView textView;

        ViewHolder(View footView){
            super(footView);
            progressBar = footView.findViewById(R.id.loading_progress);
            textView = footView.findViewById(R.id.loading_text);
        }

        void setLoadingView(){
            progressBar.setVisibility(View.VISIBLE);
            textView.setText("加载中");
        }

        void setEndView(){
            progressBar.setVisibility(View.GONE);
            textView.setText("已经没有了哦");
        }
    }

    //传入数据adapter
    public PullUpRefreshAdapter(RecyclerView.Adapter dataAdapter){
        this.dataAdapter = dataAdapter;
    }

    //设置footView样式为没有剩余数据
    public void setEndView(){
        if(isLoading) {
            isLoading = false;
            viewHolder.setEndView();
        }
    }

    //设置footView样式为加载中
    public void setLoadingView() {
        if (!isLoading){
            isLoading = true;
            viewHolder.setLoadingView();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() - 1){
            //item位置在倒数第一个位置时为footView
            return TYPE_FOOT_VIEW;
        } else {
            //其余为itemView
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOT_VIEW){
            //footView返回类内的viewHolder
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.view_foot, parent, false);
            viewHolder = new ViewHolder(view);
            return viewHolder;
        } else {
            //itemView则返回数据adapter返回的viewHolder
            return dataAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position == getItemCount() - 1){
            //footView样式根据isLoading的值进行初始化
            if(isLoading) {
                ((ViewHolder) holder).setLoadingView();
            } else {
                ((ViewHolder) holder).setEndView();
            }
        } else {
            //itemView样式由数据adapter决定
            dataAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        //item数目为数据item数据+1
        return dataAdapter.getItemCount() + 1;
    }

}
