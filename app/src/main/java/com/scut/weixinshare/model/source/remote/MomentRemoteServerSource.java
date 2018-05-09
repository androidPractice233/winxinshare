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

//实现从远程服务器获取动态数据
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
                //检查请求是否成功
                if(resultBean.getCode() == 200) {
                    final List<MomentVersion> momentVersionList =
                            (List<MomentVersion>) resultBean.getData();
                    callback.onMomentVersionsLoaded(momentVersionList);
                } else {
                    callback.onDataNotAvailable(resultBean.getMsg());
                }
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
                //检查请求是否成功
                if(resultBean.getCode() == 200) {
                    List<List<Object>> data = (List<List<Object>>) resultBean.getData();
                    if (data != null && data.size() > 0) {
                        List<Object> momentData = data.get(0);
                        if (momentData != null && momentData.size() > 0) {
                            Moment moment = (Moment) momentData.get(0);
                            if (momentData.size() > 1) {
                                List<Comment> comments = new ArrayList<>();
                                for (int i = 1; i < momentData.size(); ++i) {
                                    comments.add((Comment) momentData.get(i));
                                }
                                //服务器获取评论按照事件降序，需要反序
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
                } else {
                    callback.onDataNotAvailable(resultBean.getMsg());
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
                //检查请求是否成功
                if(resultBean.getCode() == 200) {
                    List<List<Object>> data = (List<List<Object>>) resultBean.getData();
                    List<Moment> momentsFromRemote = new ArrayList<>();
                    for (List<Object> momentData : data) {
                        if (momentData != null && momentData.size() > 0) {
                            Moment moment = (Moment) momentData.get(0);
                            if (momentData.size() > 1) {
                                List<Comment> comments = new ArrayList<>();
                                for (int i = 1; i < momentData.size(); ++i) {
                                    comments.add((Comment) momentData.get(i));
                                }
                                //服务器获取评论按照事件降序，需要反序
                                Collections.reverse(comments);
                                moment.setCommentList(comments);
                            }
                            momentsFromRemote.add(moment);
                        }
                    }
                    callback.onMomentsLoaded(momentsFromRemote);
                } else {
                    callback.onDataNotAvailable(resultBean.getMsg());
                }
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
                //检查请求是否成功
                if(resultBean.getCode() == 200) {
                    final String momentId = (String) resultBean.getData();
                    if (imageFiles != null) {
                        //创建的动态包含图片，则另外上传图片
                        //若图片上传失败，由于文字动态已创建，返回动态创建成功
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
                                    callback.onSuccess();
                                }
                            }, momentId, imageFiles);
                        } catch (IOException e) {
                            //callback.onFailure("图片上传失败，请检查文件是否存在");
                            callback.onSuccess();
                        }
                    } else {
                        callback.onSuccess();
                    }
                } else {
                    callback.onFailure(resultBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }, text, location);
    }

    @Override
    public void createComment(final String text, final String momentId, final String receiverId,
                              final CreateCommentCallback callback) {
        NetworkManager.getInstance().createComment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                //检查请求是否成功
                if(resultBean.getCode() == 200) {
                    String commentId = (String) resultBean.getData();
                    callback.onSuccess();
                } else {
                    callback.onFailure(resultBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }, momentId, receiverId, text);
    }

    @Override
    public void getSomebodyMoments(String personId,   int pageNum, int pageSize, final GetPersonMomentsCallback callback) {
        NetworkManager.getInstance().requestPersonMoment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                if(resultBean.getCode() == 200) {
                    final List<MomentVersion> momentVersionList =
                            (List<MomentVersion>) resultBean.getData();
                    callback.onMomentVersionsLoaded(momentVersionList);
                }
                else {
                    callback.onDataNotAvailable(resultBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onDataNotAvailable(t.getMessage());
            }
        }, personId, pageNum, pageSize);
    }

    @Override
    public void getMomentUserData(List<String> userId, final GetMomentUserDataCallback callback) {
        NetworkManager.getInstance().requestNicknameAndPortrait(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                //检查请求是否成功
                if(resultBean.getCode() == 200) {
                    List<MomentUserData> userDataList = (List<MomentUserData>) resultBean.getData();
                    callback.onUserDataLoaded(userDataList);
                } else {
                    callback.onFailure(resultBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }, userId);
    }

}
