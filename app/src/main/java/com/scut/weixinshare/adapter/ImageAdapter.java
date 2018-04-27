package com.scut.weixinshare.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.view.component.SquareImageView;

import java.util.List;

//动态发布界面的图片recyclerView的adapter
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    //图片uri列表
    private List<Uri> imageList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        SquareImageView imageView;

        ViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

    public ImageAdapter(List<Uri> imageList){
        this.imageList = imageList;
    }

    //设置图片uri列表
    public void setAdapterData(List<Uri> imageList){
        this.imageList = imageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_pic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri imageUri = imageList.get(position);
        Glide.with(MyApplication.getContext()).load(imageUri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
