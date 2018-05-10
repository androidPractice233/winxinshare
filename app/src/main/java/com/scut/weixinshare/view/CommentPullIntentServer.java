package com.scut.weixinshare.view;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.scut.weixinshare.db.DBOperator;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.retrofit.BaseCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CommentPullIntentServer extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.scut.weixinshare.view.action.FOO";
    private static final String ACTION_BAZ = "com.scut.weixinshare.view.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.scut.weixinshare.view.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.scut.weixinshare.view.extra.PARAM2";

    public CommentPullIntentServer() {
        super("CommentPullIntentServer");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, CommentPullIntentServer.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, CommentPullIntentServer.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("PullCommentService","Thread id is"+Thread.currentThread().getId());
        Message message = new Message();
        message.what = MainActivity.UPDATE_COMMENT_NUM;
        message.arg1 = 5;
        //MainActivity.handler.sendMessage(message);


        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }

        DBOperator dbOperator = new DBOperator();
        String lastUpdateTime = dbOperator.getLastTime();
        dbOperator.close();
        if(lastUpdateTime==null){
            lastUpdateTime = System.currentTimeMillis()+"";
        }

        Callback<ResultBean> callback = new BaseCallback() {

            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean=  response.body();
                if(resultBean==null){
                    //Toast.makeText(MainActivity.this,"resultBean is null",Toast.LENGTH_LONG).show();
                    return;
                }
                String data = resultBean.getData().toString();
                Log.d("pullComment",data);
                //Toast.makeText(MainActivity.this,data,Toast.LENGTH_LONG).show();
                //更新最近评论时间
                //更新提示信息数量
                //
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                Log.d("pullComment",t.getMessage());
            }
        };
        while(true) {
            //Log.d("pullComment","start");
            if(MainActivity.TOKEN!=null) {
                //NetworkManager.getInstance().pullComment(callback,TOKEN,lastUpdateTime);
                Log.d("pullComment", "token");
            }else {
                Log.d("pullComment", "token is null");
                //Toast.makeText(MainActivity.this,"token is null",Toast.LENGTH_LONG).show();

            }
            try {
                //每三十秒请求一次
                Thread.sleep(1000*30);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
