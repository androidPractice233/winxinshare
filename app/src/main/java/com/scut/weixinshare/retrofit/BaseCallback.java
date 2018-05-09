package com.scut.weixinshare.retrofit;


import android.content.Context;
import android.widget.Toast;

import com.scut.weixinshare.MyApplication;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.utils.AES;
import com.scut.weixinshare.view.LoginActivity;

import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.type;

/**
 * Created by skyluo on 2018/4/15.
 */

public abstract class BaseCallback implements Callback<ResultBean> {
    /**
     * 检验各种错误情况,200为true，其他情况返回false
     *
     * @return
     */
    protected boolean checkResult(Context context, ResultBean resultBean) {

        if (resultBean.getCode() == 401) {
            Toast.makeText(context, "Token失效，请重新登陆", Toast.LENGTH_SHORT).show();
//            LoginActivity.actionStart(context);
            return false;
        }
        if (resultBean.getCode() == 200) {
            return true;
        } else return false;
    }

    protected ResultBean getResultBean(Response<ResultBean> response) {
        ResultBean resultBean = new ResultBean();
        if (response.code() != 200) {
            resultBean = new ResultBean();
            resultBean.setCode(response.code());
            try {
                resultBean.setMsg(response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultBean;
        }
        else return response.body();
    }

    }

