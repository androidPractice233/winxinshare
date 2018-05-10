package com.scut.weixinshare.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.R;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.MomentsRepository;
import com.scut.weixinshare.model.source.local.MomentDatabaseSource;
import com.scut.weixinshare.model.source.remote.MomentRemoteServerSource;
import com.scut.weixinshare.presenter.MomentDetailPresenter;
import com.scut.weixinshare.utils.ToastUtils;
import com.scut.weixinshare.view.fragment.MomentDetailFragment;

//动态正文界面，包含评论功能
public class MomentDetailActivity extends AppCompatActivity {

    private MomentDetailPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_detail);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MomentDetailFragment fragment = (MomentDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_moment_detail);
        //接收传递的动态正文信息
        Intent intent = getIntent();
        String momentId = intent.getStringExtra("momentId");
        boolean isToComment = intent.getBooleanExtra("isToComment", false);
            presenter = new MomentDetailPresenter(fragment, momentId, isToComment,
                    MomentsRepository.getInstance(MomentDatabaseSource.getInstance(),
                            MomentRemoteServerSource.getInstance()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //向上一个活动返回修改后的动态信息
        setResult(RESULT_OK, presenter.returnData());
        finish();
    }

    public static void activityStart(Context context, String momentId, boolean isToComment){
        Intent intent = new Intent(context, MomentDetailActivity.class);
        intent.putExtra("momentId", momentId);
        intent.putExtra("isToComment", isToComment);
        context.startActivity(intent);
    }

    //主页可通过返回信息更新主页对应动态
    public static void activityStartForResult(Fragment fragment, String momentId,
                                              boolean isToComment){
        Intent intent = new Intent(fragment.getContext(), MomentDetailActivity.class);
        intent.putExtra("momentId", momentId);
        intent.putExtra("isToComment", isToComment);
        fragment.startActivityForResult(intent, IConst.REQUEST_CODE_MOMENT_DETAIL);
    }
}
