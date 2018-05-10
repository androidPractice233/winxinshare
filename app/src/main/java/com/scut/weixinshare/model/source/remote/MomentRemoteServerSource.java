package com.scut.weixinshare.model.source.remote;

import android.net.Uri;

import com.luck.picture.lib.tools.StringUtils;
import com.scut.weixinshare.IConst;
import com.scut.weixinshare.manager.NetworkManager;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.ResultBean;
import com.scut.weixinshare.model.source.MomentUserData;
import com.scut.weixinshare.model.source.MomentVersion;
import com.scut.weixinshare.utils.MomentUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                if(resultBean != null) {
                    if (resultBean.getCode() == IConst.ERROR_CODE_SUCCESS) {
                        //final List<MomentVersion> momentVersionList =
                        //        (List<MomentVersion>) resultBean.getData();
                        List<MomentVersion> momentVersionList = new ArrayList<>();
                        List<Map> data = (List<Map>) resultBean.getData();
                        for (Map momentVersion : data) {
                            momentVersionList.add(mapToMomentVersion(momentVersion));
                        }
                        callback.onMomentVersionsLoaded(momentVersionList);
                    } else if(resultBean.getCode() == IConst.ERROR_CODE_EMPTY_MOMENT){
                        //没有更新动态，则返回空列表
                        callback.onMomentVersionsLoaded(new ArrayList<MomentVersion>());
                    } else {
                        callback.onDataNotAvailable(resultBean.getCode() + ":" + resultBean.getMsg());
                    }
                } else {
                    callback.onDataNotAvailable("服务器应答为空");
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
                if(resultBean != null) {
                    if (resultBean.getCode() == IConst.ERROR_CODE_SUCCESS) {
                        List<List<Map>> data = (List<List<Map>>) resultBean.getData();
                        if (data != null && data.size() > 0) {
                            List<Map> momentData = data.get(0);
                            if (momentData != null && momentData.size() > 0) {
                                Moment moment = mapToMoment(momentData.get(0));
                                if (momentData.size() > 1) {
                                    List<Comment> comments = new ArrayList<>();
                                    for (int i = 1; i < momentData.size(); ++i) {
                                        comments.add(mapToComment(momentData.get(i)));
                                    }
                                    //服务器获取评论按照事件降序，需要反序
                                    //Collections.reverse(comments);
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
                        callback.onDataNotAvailable(resultBean.getCode() + ":" + resultBean.getMsg());
                    }
                } else {
                    callback.onDataNotAvailable("服务器应答为空");
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
                if(resultBean != null) {
                    if (resultBean.getCode() == IConst.ERROR_CODE_SUCCESS) {
                        List<List<Map>> data = (List<List<Map>>) resultBean.getData();
                        List<Moment> momentsFromRemote = new ArrayList<>();
                        for (List<Map> momentData : data) {
                            if (momentData != null && momentData.size() > 0) {
                                Moment moment = mapToMoment(momentData.get(0));
                                if (momentData.size() > 1) {
                                    List<Comment> comments = new ArrayList<>();
                                    for (int i = 1; i < momentData.size(); ++i) {
                                        comments.add(mapToComment(momentData.get(i)));
                                    }
                                    //服务器获取评论按照事件降序，需要反序
                                    //Collections.reverse(comments);
                                    moment.setCommentList(comments);
                                }
                                momentsFromRemote.add(moment);
                            }
                        }
                        callback.onMomentsLoaded(momentsFromRemote);
                    } else {
                        callback.onDataNotAvailable(resultBean.getCode() + ":" + resultBean.getMsg());
                    }
                } else {
                    callback.onDataNotAvailable("服务器应答为空");
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onDataNotAvailable(t.getMessage());
            }
        }, momentIds);
    }

    @Override
    public void createMoment(String userId, Location location, String text, final List<File> imageFiles,
                             final CreateMomentCallback callback) {
        NetworkManager.getInstance().createMoment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                //检查请求是否成功
                if(resultBean != null) {
                    if (resultBean.getCode() == IConst.ERROR_CODE_SUCCESS) {
                        Map map = (Map) resultBean.getData();
                        final String momentId = (String) map.get("momentId");
                        if (imageFiles != null) {
                            //创建的动态包含图片，则另外上传图片
                            //若图片上传失败，由于文字动态已创建，返回动态创建成功
                            try {
                                NetworkManager.getInstance().uploadMomentImages(new Callback<ResultBean>() {
                                    @Override
                                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                                        //ResultBean resultBean = response.body();
                                        //List<Map> data = (List<Map>) resultBean.getData();
                                        //List<Uri> imageUriList = (List<Uri>) resultBean.getData();
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
                        callback.onFailure(resultBean.getCode() + ":" + resultBean.getMsg());
                    }
                } else {
                    callback.onFailure("服务器应答为空");
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }, userId, text, location);
    }

    @Override
    public void createComment(String text, String momentId, String receiverId, String sendId,
                              final CreateCommentCallback callback) {
        NetworkManager.getInstance().createComment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                //检查请求是否成功
                if(resultBean != null) {
                    if (resultBean.getCode() == IConst.ERROR_CODE_SUCCESS) {
                        //String commentId = (String) resultBean.getData();
                        callback.onSuccess();
                    } else {
                        callback.onFailure(resultBean.getCode() + ":" + resultBean.getMsg());
                    }
                } else {
                    callback.onFailure("服务器应答为空");
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }

        }, momentId, sendId, receiverId, text);
    }

    @Override
    public void getSomebodyMoments(String personId, int pageNum, int pageSize, final GetPersonMomentsCallback callback) {
        NetworkManager.getInstance().requestPersonMoment(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                ResultBean resultBean = response.body();
                if(resultBean != null) {
                    if (resultBean.getCode() == IConst.ERROR_CODE_SUCCESS) {
                        List<Map> data = (List<Map>) resultBean.getData();
                        List<MomentVersion> momentVersionList = new ArrayList<>();
                        for (Map momentVersion : data) {
                            momentVersionList.add(mapToMomentVersion(momentVersion));
                        }
                        callback.onMomentVersionsLoaded(momentVersionList);
                    } else {
                        callback.onDataNotAvailable(resultBean.getCode() + ":" + resultBean.getMsg());
                    }
                } else {
                    callback.onDataNotAvailable("服务器应答为空");
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
                if(resultBean != null) {
                    if (resultBean.getCode() == IConst.ERROR_CODE_SUCCESS) {
                        List<Map> data = (List<Map>) resultBean.getData();
                        List<MomentUserData> userDataList = new ArrayList<>();
                        for (Map userData : data) {
                            userDataList.add(mapToMomentUserData(userData));
                        }
                        callback.onUserDataLoaded(userDataList);
                    } else {
                        callback.onFailure(resultBean.getCode() + ":" + resultBean.getMsg());
                    }
                } else {
                    callback.onFailure("服务器应答为空");
                }
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        }, userId);
    }

    private MomentVersion mapToMomentVersion(Map data){
        return new MomentVersion((String) data.get("momentId"),
                new Timestamp(Math.round((double) data.get("updateTime"))));
    }

    private Moment mapToMoment(Map data){
        Moment moment = new Moment((String) data.get("momentId"), (String) data.get("userId"),
                (String) data.get("nickName"), null,
                new Timestamp(Math.round((double) data.get("createTime"))),
                (String) data.get("location"), (String) data.get("textContent"), null,
                null, new Timestamp(Math.round((double) data.get("updateTime"))));
        if(data.get("portrait") != null){
            moment.setPortrait(MomentUtils.StringToUri((String) data.get("portrait")));
        }
        String picContent = (String) data.get("picContent");
        if(picContent != null && !"".equals(picContent)){
            List<Uri> imageList = MomentUtils.imageUriStringToList(picContent);
            moment.setPicContent(imageList);
        }
        return moment;
    }

    private Comment mapToComment(Map data){
        Comment comment = new Comment((String) data.get("commendId"), (String) data.get("sendId"),
                (String) data.get("recvId"), (String) data.get("sendNickName"),
                null, (String) data.get("recvNickName"), (String) data.get("content"),
                new Timestamp(Math.round((double) data.get("createTime"))));
        if(data.get("portrait") != null){
            comment.setPortrait(MomentUtils.StringToUri((String) data.get("portrait")));
        }
        return comment;
    }

    private MomentUserData mapToMomentUserData(Map data){
        return new MomentUserData((String) data.get("userId"), (String) data.get("nickName"),
                MomentUtils.StringToUri((String) data.get("portrait")));
    }

}
