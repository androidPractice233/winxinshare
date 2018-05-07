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
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Moment moment = bundle.getParcelable("moment");
            boolean isToComment = bundle.getBoolean("isToComment", false);
            presenter = new MomentDetailPresenter(fragment, moment, isToComment,
                    MomentsRepository.getInstance());
        } else {
            ToastUtils.showToast(this, "信息不足");
            finish();
        }
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
        Intent intent = new Intent();
        intent.putExtras(presenter.resultToHome());
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void activityStart(Context context, Moment moment, boolean isToComment){
        Bundle bundle = new Bundle();
        bundle.putParcelable("moment", moment);
        bundle.putBoolean("isToComment", isToComment);
        Intent intent = new Intent(context, MomentDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void activityStartForResult(Fragment fragment, Moment moment,
                                              boolean isToComment){
        Bundle bundle = new Bundle();
        bundle.putParcelable("moment", moment);
        bundle.putBoolean("isToComment", isToComment);
        Intent intent = new Intent(fragment.getContext(), MomentDetailActivity.class);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent, IConst.REQUEST_CODE_MOMENT_DETAIL);
    }
}
