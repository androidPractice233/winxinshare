package com.scut.weixinshare.view.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.adapter.MomentAdapter;
import com.scut.weixinshare.adapter.PullUpRefreshAdapter;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.utils.DensityUtils;

import java.util.ArrayList;

/**
 * Created by skyluo on 2018/5/9.
 */

public class PersonHomeFragment extends HomeFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_home, container, false);
        recyclerView = view.findViewById(R.id.list_moments);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        momentAdapter = new MomentAdapter(new ArrayList<Moment>(), this);
        pullUpRefreshAdapter = new PullUpRefreshAdapter(momentAdapter,
                new PullUpRefreshAdapter.NetworkErrorTextOnClickListener() {
                    @Override
                    public void onClick() {
                        presenter.breakErrorState();
                        presenter.requestNextMoments();
                    }
                });
        recyclerView.setAdapter(pullUpRefreshAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());     //设置RecyclerView动画
        //设置Item间距，实现类卡片式Item效果
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = DensityUtils.dipToPx(MyApplication.getContext(),
                        MARGIN_TOP_CARD);
                outRect.bottom = DensityUtils.dipToPx(MyApplication.getContext(),
                        MARGIN_BOTTOM_CARD);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if(fab != null) {
                    //上拉隐藏fab，下拉显示fab
                    if (dy > 0) {
                        fab.hide();
                    } else if (dy < 0) {
                        fab.show();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    LinearLayoutManager manager = (LinearLayoutManager)recyclerView.
                            getLayoutManager();
                    //当前已加载但未读动态少于10条时，提前向服务器请求后续动态
                    if(manager.findLastCompletelyVisibleItemPosition() >=
                            manager.getItemCount() - 11){
                        presenter.requestNextMoments();
                    }
                }
            }
        });
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        //下拉触发向服务器请求新动态
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.requestNewMoments();
            }
        });
        presenter.requestNewMoments();
        return view;
    }
}
