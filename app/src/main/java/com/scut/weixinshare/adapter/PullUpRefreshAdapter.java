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
    //区分footView状态
    private static final int NETWORK_ERROR = 0;
    private static final int LOADING = 1;
    private static final int END = 2;

    private NetworkErrorTextOnClickListener listener;
    private RecyclerView.Adapter dataAdapter;    //recyclerView数据adapter
    private ViewHolder viewHolder;               //ootItem的viewHolder
    private int state = LOADING;                 //设定FootView状态

    static class ViewHolder extends RecyclerView.ViewHolder{

        View loadingView;
        View endView;
        View networkErrorView;
        TextView networkErrorText;

        ViewHolder(View footView){
            super(footView);
            loadingView = footView.findViewById(R.id.loading_view);
            endView = footView.findViewById(R.id.end_view);
            networkErrorView = footView.findViewById(R.id.network_error_view);
            networkErrorText = footView.findViewById(R.id.network_error_text);
        }

        void setLoadingView(){
            loadingView.setVisibility(View.VISIBLE);
            endView.setVisibility(View.GONE);
            networkErrorView.setVisibility(View.GONE);
        }

        void setEndView(){
            loadingView.setVisibility(View.GONE);
            endView.setVisibility(View.VISIBLE);
            networkErrorView.setVisibility(View.GONE);
        }

        void setNetworkErrorView(){
            loadingView.setVisibility(View.GONE);
            endView.setVisibility(View.GONE);
            networkErrorView.setVisibility(View.VISIBLE);
        }

    }

    //传入数据adapter
    public PullUpRefreshAdapter(RecyclerView.Adapter dataAdapter,
                                NetworkErrorTextOnClickListener listener){
        this.dataAdapter = dataAdapter;
        this.listener = listener;
    }

    //设置footView样式为没有剩余数据
    public void setEndView(){
        if(state != END) {
            state = END;
            viewHolder.setEndView();
        }
    }

    //设置footView样式为加载中
    public void setLoadingView() {
        if (state != LOADING){
            state = LOADING;
            viewHolder.setLoadingView();
        }
    }

    public void setNetworkErrorView(){
        if(state != NETWORK_ERROR){
            state = NETWORK_ERROR;
            viewHolder.setNetworkErrorView();
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
            switch(state) {
                case LOADING:
                    ((ViewHolder) holder).setLoadingView();
                    break;
                case END:
                    ((ViewHolder) holder).setEndView();
                    break;
                case NETWORK_ERROR:
                    ((ViewHolder) holder).setNetworkErrorView();
                    break;
                default:
                    break;
            }
            ((ViewHolder) holder).networkErrorText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick();
                }
            });
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

    public interface NetworkErrorTextOnClickListener{
        void onClick();
    }

}
