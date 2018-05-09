package com.scut.weixinshare.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.scut.weixinshare.R;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.retrofit.BaseCallback;
import com.scut.weixinshare.view.MainActivity;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by skyluo on 2018/4/16.
 */

public class MainFragment  extends Fragment{
    private Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_main,container,false);
        button= (Button) view.findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkManager.getInstance().test(new BaseCallback() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                       ResultBean resultBean=getResultBean(response);
                        if(this.checkResult(getContext(),resultBean)) {
//                           Toast.makeText(getContext(), (String) resultBean.getData(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {
                        Log.e("MainActivity", t.getMessage()  );
                    }
                });
            }
        });
        return  view;
    }
}
