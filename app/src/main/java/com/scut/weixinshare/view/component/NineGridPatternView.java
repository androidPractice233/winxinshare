package com.scut.weixinshare.view.component;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

//实现九宫格图片显示，xml文件中layout_width必须为固定宽度或者match_parent
public class NineGridPatternView extends GridLayout implements View.OnClickListener {

    //保存已创建的ImageView，避免重复删除添加ImageView，减少卡顿
    private List<ImageView> imageViews = new ArrayList<>();
    private List<Uri> uriList;
    private NineGridPatternViewListener listener;
    private int margin = 8;     //默认图片间距为8dp

    public NineGridPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setOnClickListener(this);
    }

    //设置图片间距
    public void setMargin(int margin){
        this.margin = margin;
    }

    public void setListener(NineGridPatternViewListener listener){
        this.listener = listener;
    }

    //设置图片显示
    public void setView(List<Uri> images) {
        //最多显示九张图片
        this.uriList = images;
        final int numImages = uriList.size() > 9 ? 9 : uriList.size();
        if (numImages > imageViews.size()) {
            for (int i = 0; i < imageViews.size(); ++i) {
                GlideUtils.loadImageView(getContext(), uriList.get(i),
                        imageViews.get(i));
                imageViews.get(i).setVisibility(View.VISIBLE);
            }
            //为了保证调用getMeasuredWidth()方法能够得到正确的宽度，在post()方法内添加ImageView
            post(new Runnable() {
                @Override
                public void run() {
                    int pos = imageViews.size();
                    //当前ImageView数目少于需要显示的图片数目，添加ImageView
                    addImageView(numImages - pos);
                    for (int i = pos; i < imageViews.size(); ++i) {
                        GlideUtils.loadImageView(getContext(), uriList.get(i),
                                imageViews.get(i));
                        imageViews.get(i).setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            for (int i = 0; i < imageViews.size(); ++i) {
                if (i < numImages) {
                    GlideUtils.loadImageView(getContext(), uriList.get(i),
                            imageViews.get(i));
                    imageViews.get(i).setVisibility(View.VISIBLE);
                } else {
                    //将不需要显示的ImageView设置为GONE
                    imageViews.get(i).setVisibility(View.GONE);
                }
            }
        }
    }

    //添加ImageView
    private void addImageView(int num){
        for(int i = 0; i < num; ++i){
            SquareImageView imageView = new SquareImageView(MyApplication.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //设置ImageView在Layout中的位置
            GridLayout.Spec rowSpec = GridLayout.spec(imageViews.size() / 3);
            GridLayout.Spec columnSpec = GridLayout.spec(imageViews.size() % 3);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            //设置ImageView宽度填满Layout
            params.width = getMeasuredWidth() / 3 - margin;
            //设置ImageView的上、左间距
            params.leftMargin = margin;
            params.topMargin = margin;
            //添加ImageView并保存引用
            addView(imageView, params);
            imageViews.add(imageView);
        }
    }

    @Override
    public void onClick(View view) {
        if(listener != null) {
            switch (view.getId()) {
                default:
                    listener.onItemClick(uriList);
                    break;
            }
        }
    }

    interface NineGridPatternViewListener{

        void onItemClick(List<Uri> uriList);
    }

}
