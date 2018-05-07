package com.scut.weixinshare.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.R;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.presenter.ReleaseMomentPresenter;
import com.scut.weixinshare.view.fragment.ReleaseMomentFragment;

public class ReleaseMomentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_moment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ReleaseMomentFragment fragment = (ReleaseMomentFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_release_moment);
        new ReleaseMomentPresenter(fragment, (Location) getIntent()
                .getParcelableExtra("location"));
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
    public void onBackPressed(){
        setResult(RESULT_CANCELED);
        finish();
    }

    public static void activityStartForResult(Fragment fragment, Location location){
        Intent intent = new Intent(fragment.getContext(), ReleaseMomentActivity.class);
        intent.putExtra("location", location);
        fragment.startActivityForResult(intent, IConst.REQUEST_CODE_RELEASE_MOMENT);
    }

}
