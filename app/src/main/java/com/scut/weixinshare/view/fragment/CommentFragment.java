package com.scut.weixinshare.view.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.R;
import com.scut.weixinshare.adapter.CommentAdapter;
import com.scut.weixinshare.db.Comment;
import com.scut.weixinshare.db.DBOperator;
import com.scut.weixinshare.utils.DensityUtils;
import com.scut.weixinshare.view.CommentPullIntentServer;

import java.util.ArrayList;
import java.util.List;


public class CommentFragment extends Fragment {
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DBOperator dbOperator;
    private List<Comment> comments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        //从数据库获取
        dbOperator = new DBOperator();
        comments = new ArrayList<>();


        recyclerView = view.findViewById(R.id.list_coments);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        commentAdapter = new CommentAdapter(comments,dbOperator);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());     //设置RecyclerView动画
        //设置Item间距，实现类卡片式Item效果
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = DensityUtils.dipToPx(MyApplication.getContext(),
                        8);
                outRect.bottom = DensityUtils.dipToPx(MyApplication.getContext(),
                        4);
            }
        });



        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_comment);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //更新comment list
                commentAdapter.updateComments();
                Toast.makeText(getContext(),"已更新到最新",Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        dbOperator.close();
    }


}
