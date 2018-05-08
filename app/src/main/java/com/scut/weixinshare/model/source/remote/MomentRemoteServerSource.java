package com.scut.weixinshare.model.source.remote;

import android.net.Uri;

import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.source.MomentUserData;
import com.scut.weixinshare.model.source.MomentVersion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MomentRemoteServerSource implements MomentRemoteSource {

    private static MomentRemoteServerSource INSTANCE = new MomentRemoteServerSource();

    //单例，线程安全
    public static MomentRemoteServerSource getInstance(){
        return INSTANCE;
    }

    @Override
    public void getNearbyMoments(Location location, int pageNum, int pageSize,
                                 final GetNearbyMomentsCallback callback) {
        NetworkManager.getInstance().requestNearbyMoment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                final List<MomentVersion> momentVersionList =
                        (List<MomentVersion>) resultBean.getData();
                callback.onMomentVersionsLoaded(momentVersionList);
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onDataNotAvailable(t.getMessage());
            }
        }, location, pageNum, pageSize);
    }

    @Override
    public void getMoment(String momentId, final GetMomentCallback callback) {
        List<String> momentIds = new ArrayList<>();
        momentIds.add(momentId);
        NetworkManager.getInstance().requestMomentDetail(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                List<List<Object>> data = (List<List<Object>>) resultBean.getData();
                if(data != null && data.size() > 0){
                    List<Object> momentData = data.get(0);
                    if(momentData != null && momentData.size() > 0){
                        Moment moment = (Moment) momentData.get(0);
                        if(momentData.size() > 1){
                            List<Comment> comments = new ArrayList<>();
                            for(int i = 1; i < momentData.size(); ++i){
                                comments.add((Comment) momentData.get(i));
                            }
                            Collections.reverse(comments);
                            moment.setCommentList(comments);
                        }
                        callback.onMomentLoaded(moment);
                    } else {
                        callback.onDataNotAvailable("动态已被删除");
                    }
                } else {
                    callback.onDataNotAvailable("动态已被删除");
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onDataNotAvailable(t.getMessage());
            }
        }, momentIds);
    }

    @Override
    public void getMoments(List<String> momentIds, final GetMomentsCallback callback) {
        NetworkManager.getInstance().requestMomentDetail(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                List<List<Object>> data = (List<List<Object>>) resultBean.getData();
                List<Moment> momentsFromRemote = new ArrayList<>();
                for(List<Object> momentData : data){
                    if(momentData != null && momentData.size() > 0){
                        Moment moment = (Moment) momentData.get(0);
                        if(momentData.size() > 1){
                            List<Comment> comments = new ArrayList<>();
                            for(int i = 1; i < momentData.size(); ++i){
                                comments.add((Comment) momentData.get(i));
                            }
                            Collections.reverse(comments);
                            moment.setCommentList(comments);
                        }
                        momentsFromRemote.add(moment);
                    }
                }
                callback.onMomentsLoaded(momentsFromRemote);
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onDataNotAvailable(t.getMessage());
            }
        }, momentIds);
    }

    @Override
    public void createMoment(Location location, String text, final List<File> imageFiles,
                             final CreateMomentCallback callback) {
        NetworkManager.getInstance().createMoment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                final String momentId = (String) resultBean.getData();
                if(imageFiles != null){
                    try {
                        NetworkManager.getInstance().uploadMomentImages(new Callback<ResultBean>() {
                            @Override
                            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                                ResultBean resultBean = response.body();
                                List<Uri> imageUriList = (List<Uri>) resultBean.getData();
                                callback.onSuccess();
                            }

                            @Override
                            public void onFailure(Call<ResultBean> call, Throwable t) {
                                callback.onFailure(t.getMessage());
                            }
                        }, momentId, imageFiles);
                    } catch (IOException e){
                        //callback.onFailure("图片上传失败，请检查文件是否存在");
                        callback.onSuccess();
                    }
                } else {
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }, text, location);
    }

    @Override
    public void createComment(final String text, final String momentId, final String senderId,
                              final String receiverId, final CreateCommentCallback callback) {
        NetworkManager.getInstance().createComment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                String commentId = (String) resultBean.getData();
                callback.onSuccess();
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }, momentId, senderId, receiverId, text);
    }

    @Override
    public void getMomentUserData(List<String> userId, final GetMomentUserDataCallback callback) {
        NetworkManager.getInstance().requestNicknameAndPortrait(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                List<MomentUserData> userDataList = (List<MomentUserData>) resultBean.getData();
                callback.onUserDataLoaded(userDataList);
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }, userId);
    }

}