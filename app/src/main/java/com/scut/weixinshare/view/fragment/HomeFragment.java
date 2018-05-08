package com.scut.weixinshare.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.adapter.MomentAdapter;
import com.scut.weixinshare.adapter.PullUpRefreshAdapter;
import com.scut.weixinshare.contract.HomeContract;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.utils.DensityUtils;
import com.scut.weixinshare.utils.ToastUtils;
import com.scut.weixinshare.view.MomentDetailActivity;
import com.scut.weixinshare.view.ReleaseMomentActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements HomeContract.View,
        MomentAdapter.MomentItemListener {
    public final static int MARGIN_TOP_CARD = 8;
    public final static int MARGIN_BOTTOM_CARD = 4;

    private HomeContract.Presenter presenter;
    private MomentAdapter momentAdapter;
    private PullUpRefreshAdapter pullUpRefreshAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
        //获取Activity中的fab
        Activity activity = getActivity();
        if(activity != null) {
            fab = activity.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.editReleaseMoment();
                }
            });
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null) {
            presenter.start();
        }
    }

    @Override
    public void addMoments(List<Moment> momentList) {
        momentAdapter.addAdapterData(momentList);
        pullUpRefreshAdapter.notifyItemRangeInserted(
                momentAdapter.getItemCount() - momentList.size() - 1,
                momentList.size());
    }

    @Override
    public void initMoments(final List<Moment> momentList) {
        momentAdapter.setAdapterData(momentList);
        pullUpRefreshAdapter.notifyItemRangeChanged(0, momentAdapter.getItemCount());
    }

    @Override
    public void showRefreshing() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideRefreshing() { swipeRefresh.setRefreshing(false); }

    @Override
    public void setListEndView(){
        pullUpRefreshAdapter.setEndView();
    }

    @Override
    public void setListLoadingView(){
        pullUpRefreshAdapter.setLoadingView();
    }

    @Override
    public void setListErrorView() {
        pullUpRefreshAdapter.setNetworkErrorView();
    }

    @Override
    public void showReleaseMomentUI(Location location) {
        ReleaseMomentActivity.activityStartForResult(this, location);
    }

    @Override
    public void showMomentDetailUI(Moment moment, boolean isToComment) {
        MomentDetailActivity.activityStartForResult(this, moment, isToComment);
    }

    @Override
    public void showUserDataUI(String momentId) {

    }

    @Override
    public void showReminderMessage(String text) {
        ToastUtils.showToast(getContext(), text);
    }

    @Override
    public void showMomentList() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMomentList() {
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void updateMomentView(Moment moment, int position) {
        momentAdapter.updateAdapterData(moment, position);
        pullUpRefreshAdapter.notifyItemChanged(position);
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onPortraitClick(Moment moment, int position) {
        presenter.openUserData(moment, position);
    }

    @Override
    public void onNickNameClick(Moment moment, int position) {
        presenter.openUserData(moment, position);
    }

    @Override
    public void onItemClick(Moment moment, int position) {
        presenter.openMomentDetail(moment, position);
    }

    @Override
    public void onAddCommentButtonClick(Moment moment, int position) {
        presenter.releaseComment(moment, position);
    }
}
