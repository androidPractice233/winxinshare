package com.scut.weixinshare.model.source;

import android.net.Uri;

import com.scut.weixinshare.db.DBOperator;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.ResultBean;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MomentsRepository implements MomentDataSource {

    private static MomentsRepository INSTANCE;

    private Map<String, Moment> momentMap;

    //单例，非线程安全，请在主线程中调用
    public static MomentsRepository getInstance(){
        if(INSTANCE == null){
            INSTANCE = new MomentsRepository();
        }
        return INSTANCE;
    }

    //销毁单例
    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void getMoment(String momentId, final GetMomentCallback callback) {
        if(momentMap.containsKey(momentId)){
            callback.onMomentLoaded(momentMap.get(momentId));
        } else {
            //从数据库/服务器获取
        }
    }

    @Override
    public void getMoments(Location location, int pageNum, int pageSize,
                           final GetMomentsCallback callback) {
        NetworkManager.getInstance().requestNearbyMoment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                List<MomentVersion> momentVersionList = (List<MomentVersion>) resultBean.getData();
                getMomentsFromLocal(momentVersionList, callback);
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onDataNotAvailable(t.getMessage());
            }
        }, location, pageNum, pageSize);
    }

    @Override
    public void createMoment(String text, Location location, CreateMomentCallback callback) {
        createMoment(text, location, null, callback);
    }

    @Override
    public void createMoment(final String text, final Location location, final List<File> imageFiles,
                             final CreateMomentCallback callback) {
        NetworkManager.getInstance().createMoment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                final String momentId = (String) resultBean.getData();
                if(imageFiles != null){
                    uploadMomentImages(momentId, imageFiles, new UploadMomentImagesCallback() {
                        @Override
                        public void onResponse(List<Uri> imageUriList) {
                            createMomentInLocal(momentId, location.getName(), text, imageUriList);
                            callback.onSuccess();
                        }

                        @Override
                        public void onFailure(String error) {
                            callback.onFailure(error);
                        }
                    });
                } else {
                    //将动态数据存入本地数据库
                    createMomentInLocal(momentId, location.getName(), text, null);
                }
                callback.onSuccess();
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
                //将评论数据存入本地数据库
                createCommentInLocal(commentId, momentId, senderId, receiverId, null,
                        text);
                callback.onSuccess(commentId);
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }, momentId, senderId, receiverId, text);
    }

    @Override
    public void refreshMoment(String momentId) {
        momentMap.remove(momentId);
    }

    private void createMomentInLocal(final String momentId, final String location,
                                     final String text, final List<Uri> picUris){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBOperator dbOperator = new DBOperator();
                if(picUris == null) {
                    dbOperator.insertMoment(new com.scut.weixinshare.db.Moment(momentId,
                            null, null, location,
                            picUris.toString(), text));
                } else {
                    dbOperator.insertMoment(new com.scut.weixinshare.db.Moment(momentId,
                            null, null, location, null, text));
                }
                dbOperator.close();
            }
        }).run();
    }

    private interface UploadMomentImagesCallback{

        void onResponse(List<Uri> imageUriList);

        void onFailure(String error);
    }

    private void uploadMomentImages(final String momentId, List<File> imageFiles,
                                    final UploadMomentImagesCallback callback){
        try {
            NetworkManager.getInstance().uploadMomentImages(new Callback<ResultBean>() {
                @Override
                public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                    ResultBean resultBean = response.body();
                    List<Uri> imageUriList = (List<Uri>) resultBean.getData();
                    callback.onResponse(imageUriList);
                }

                @Override
                public void onFailure(Call<ResultBean> call, Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            }, momentId, imageFiles);
        } catch (IOException e){
            callback.onFailure("图片文件加载错误，请检查文件是否存在");
        }
    }

    private void createCommentInLocal(final String commentId, final String momentId,
                                      final String senderId, final String receiverId,
                                      final Timestamp createTime, final String content){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBOperator dbOperator = new DBOperator();
                dbOperator.insertComment(new com.scut.weixinshare.db.Comment(commentId, momentId,
                        senderId, receiverId, createTime.toString(), content));
                dbOperator.close();
            }
        }).run();
    }

    private void getMomentsFromLocal(List<MomentVersion> versionList, GetMomentsCallback callback){
        //与本地数据库对比
        List<String> momentIds = new ArrayList<>();
        for(MomentVersion version : versionList){
            momentIds.add(version.momentId);
        }
        getMomentsFromRemote(versionList, momentIds, callback);
    }

    private void getMomentsFromRemote(final List<MomentVersion> versionList, final List<String> momentIds,
                                      final GetMomentsCallback callback){
        NetworkManager.getInstance().requestMomentDetail(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                List<Moment> momentsFromRemote = (List<Moment>) resultBean.getData();
                for(Moment moment : momentsFromRemote){
                    momentMap.put(moment.getMomentId(), moment);
                }
                List<Moment> moments = new ArrayList<>();
                for(MomentVersion version : versionList){
                    if(momentMap.containsKey(version.getMomentId())){
                        moments.add(momentMap.get(version.getMomentId()));
                    }
                }

                callback.onMomentsLoaded(moments);
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onDataNotAvailable(t.getMessage());
            }
        }, momentIds);
    }

    //封装动态更新信息
    private class MomentVersion{

        String momentId;
        Timestamp updateTime;

        public void setMomentId(String momentId) {
            this.momentId = momentId;
        }

        public void setUpdateTime(Timestamp updateTime) {
            this.updateTime = updateTime;
        }

        public String getMomentId() {
            return momentId;
        }

        public Timestamp getUpdateTime() {
            return updateTime;
        }
    }

}
