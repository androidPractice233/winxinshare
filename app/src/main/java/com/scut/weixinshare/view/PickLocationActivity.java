package com.scut.weixinshare.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.R;
import com.scut.weixinshare.model.source.LocationRepository;
import com.scut.weixinshare.presenter.PickLocationPresenter;
import com.scut.weixinshare.view.fragment.PickLocationFragment;

public class PickLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        PickLocationFragment fragment = (PickLocationFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_pick_location);
        new PickLocationPresenter(fragment, LocationRepository.getInstance());
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

    public static void activityStartForResult(Fragment fragment){
        Intent intent = new Intent(fragment.getContext(), PickLocationActivity.class);
        fragment.startActivityForResult(intent, IConst.REQUEST_CODE_PICK_LOCATION);
    }

}
