package com.scut.weixinshare.model.source;

import com.scut.weixinshare.IConst;
import com.scut.weixinshare.model.Comment;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.local.CommentLocal;
import com.scut.weixinshare.model.source.local.MomentLocal;
import com.scut.weixinshare.model.source.local.MomentLocalSource;
import com.scut.weixinshare.model.source.remote.MomentRemoteSource;
import com.scut.weixinshare.utils.MomentUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//实现动态数据仓库
public class MomentsRepository implements MomentDataSource {

    private static MomentsRepository INSTANCE;

    private Map<String, Moment> momentMap;               //动态数据缓存
    private Map<String, MomentUserData> userDataMap;     //用户信息缓存
    private MomentLocalSource localSource;               //本地动态数据来源
    private MomentRemoteSource remoteSource;             //远程动态数据来源

    public MomentsRepository(MomentLocalSource localSource, MomentRemoteSource remoteSource){
        this.localSource = localSource;
        this.remoteSource = remoteSource;
        momentMap = new HashMap<>();
        userDataMap = new HashMap<>();
    }

    //单例，非线程安全，请在主线程中调用
    public static MomentsRepository getInstance(MomentLocalSource localSource,
                                                MomentRemoteSource remoteSource){
        if(INSTANCE == null){
            INSTANCE = new MomentsRepository(localSource, remoteSource);
        }
        return INSTANCE;
    }

    //销毁单例
    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void getSomebodyMoments(String personId, int pageNum, int pageSize,final  GetMomentsCallback callback) {
        remoteSource.getSomebodyMoments(personId, pageNum, pageSize,
                new MomentRemoteSource.GetPersonMomentsCallback() {
                    @Override
                    public void onMomentVersionsLoaded(final List<MomentVersion> momentVersionList) {
                        if(momentVersionList != null && momentVersionList.size() > 0) {
                            localSource.getMoments(momentVersionList, new MomentLocalSource
                                    .GetMomentsCallback() {
                                @Override
                                public void onMomentsLoaded(final List<MomentLocal> moments) {
                                    final Set<String> usersWithoutPortrait = new HashSet<>();
                                    List<String> momentsNeedToRequest = new ArrayList<>();
                                    Set<String> momentsFromLocal = new HashSet<>();
                                    for(MomentLocal moment : moments){
                                        momentsFromLocal.add(moment.getMomentId());
                                        if(!userDataMap.containsKey(moment.getUserId())){
                                            usersWithoutPortrait.add(moment.getUserId());
                                        }
                                        if(moment.getCommentList() != null){
                                            for(CommentLocal comment : moment.getCommentList()){
                                                if(!userDataMap.containsKey(comment.getSenderId())){
                                                    usersWithoutPortrait.add(comment.getSenderId());
                                                }
                                            }
                                        }
                                    }
                                    for(MomentVersion momentVersion : momentVersionList){
                                        if(!momentsFromLocal.contains(momentVersion.getMomentId())){
                                            momentsNeedToRequest.add(momentVersion.getMomentId());
                                        }
                                    }
                                    remoteSource.getMoments(momentsNeedToRequest, new MomentRemoteSource.GetMomentsCallback() {
                                        @Override
                                        public void onMomentsLoaded(List<Moment> momentList) {
                                            for(Moment moment : momentList){
                                                momentMap.put(moment.getMomentId(), moment);
                                            }
                                            final List<String> userList = new ArrayList<>(usersWithoutPortrait);
                                            remoteSource.getMomentUserData(userList,
                                                    new MomentRemoteSource.GetMomentUserDataCallback() {
                                                        @Override
                                                        public void onUserDataLoaded(List<MomentUserData> userDataList) {
                                                            for(int i = 0; i < userDataList.size(); ++i){
                                                                userDataMap.put(userList.get(i), userDataList.get(i));
                                                            }
                                                            for(MomentLocal momentLocal : moments){
                                                                Moment moment = MomentUtils.momentLocalToMoment(momentLocal);
                                                                MomentUserData  userData = userDataMap.get(moment.getUserId());
                                                                moment.setUserData(userData.getNickName(), userData.getPortrait());
                                                                if(moment.getCommentList() != null){
                                                                    for(Comment comment : moment.getCommentList()){
                                                                        comment.setSenderData(userDataMap.get(comment.getSendId()));
                                                                        comment.setRecvNickName(userDataMap.get(comment.getRecvId()).getNickName());
                                                                    }
                                                                }
                                                                momentMap.put(moment.getMomentId(), moment);
                                                            }
                                                            callback.onMomentsLoaded(initMomentList(momentVersionList));
                                                        }

                                                        @Override
                                                        public void onFailure(String error) {
                                                            callback.onDataNotAvailable(error);
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onDataNotAvailable(String error) {
                                            callback.onDataNotAvailable(error);
                                        }
                                    });
                                }
                            });
                        } else {
                            callback.onMomentsLoaded(new ArrayList<Moment>());
                        }
                    }

                    @Override
                    public void onDataNotAvailable(String error) {
                        callback.onDataNotAvailable(error);
                    }
                });
    }

    @Override
    public void getMoment(String momentId, final GetMomentCallback callback) {
        //查找缓存
        if(momentMap.containsKey(momentId)){
            callback.onMomentLoaded(momentMap.get(momentId));
        } else {
            //不存在动态缓存，远程获取动态数据
            remoteSource.getMoment(momentId, new MomentRemoteSource.GetMomentCallback() {
                @Override
                public void onMomentLoaded(Moment moment) {
                    momentMap.put(moment.getMomentId(), moment);     //缓存动态数据
                    //localSource.createMoment(moment);                //本地存储动态数据
                    callback.onMomentLoaded(moment);
                }

                @Override
                public void onDataNotAvailable(String error) {
                    callback.onDataNotAvailable(error);
                }
            });
        }
    }

    @Override
    public void getMoments(final Location location, int pageNum, int pageSize,
                           final GetMomentsCallback callback) {
        //远程获取附近动态版本信息
        remoteSource.getNearbyMoments(location, pageNum, pageSize,
                new MomentRemoteSource.GetNearbyMomentsCallback() {
                    @Override
                    public void onMomentVersionsLoaded(final List<MomentVersion> momentVersionList) {
                        if(momentVersionList != null && momentVersionList.size() > 0) {
                            //检查动态是否已缓存
                            final List<MomentVersion> momentWithoutCache = new ArrayList<>();
                            //这里有bug

                            for(MomentVersion moment : momentVersionList){
                                if(!momentMap.containsKey(moment.getMomentId()) ||
                                        !momentMap.get(moment.getMomentId()).getUpdateTime()
                                                .equals(moment.getUpdateTime())){
                                    momentWithoutCache.add(moment);
                                }
                            }
                            if(momentWithoutCache.size() > 0) {
                                //从本地获取相同版本的动态
                                /*localSource.getMoments(momentWithoutCache, new MomentLocalSource
                                        .GetMomentsCallback() {
                                    @Override
                                    public void onMomentsLoaded(final List<MomentLocal> moments) {
                                        //记录缺少用户信息的userId
                                        final Set<String> usersWithoutPortrait = new HashSet<>();
                                        //记录需要远程请求数据的动态id
                                        List<String> momentsNeedToRequest = new ArrayList<>();
                                        //记录从已从本地获取动态数据的动态id
                                        Set<String> momentsFromLocal = new HashSet<>();
                                        //为本地获取的动态数据添加用户信息
                                        //如果不存在对应用户信息的缓存，则记录userId
                                        for (MomentLocal moment : moments) {
                                            momentsFromLocal.add(moment.getMomentId());
                                            if (!userDataMap.containsKey(moment.getUserId())) {
                                                usersWithoutPortrait.add(moment.getUserId());
                                            }
                                            if (moment.getCommentList() != null) {
                                                for (CommentLocal comment : moment.getCommentList()) {
                                                    if (!userDataMap.containsKey(comment.getSenderId())) {
                                                        usersWithoutPortrait.add(comment.getSenderId());
                                                    }
                                                }
                                            }
                                        }
                                        //将本地不包含最新数据的动态id，加入需要远程请求数据的动态id表中
                                        for (MomentVersion momentVersion : momentWithoutCache) {
                                            if (!momentsFromLocal.contains(momentVersion.getMomentId())) {
                                                momentsNeedToRequest.add(momentVersion.getMomentId());
                                            }
                                        }
                                        if(momentsNeedToRequest.size() > 0) {
                                            //远程请求动态信息
                                            remoteSource.getMoments(momentsNeedToRequest, new MomentRemoteSource.GetMomentsCallback() {
                                                @Override
                                                public void onMomentsLoaded(List<Moment> momentList) {
                                                    //将请求到的动态信息加入缓存
                                                    for (Moment moment : momentList) {
                                                        momentMap.put(moment.getMomentId(), moment);
                                                    }
                                                    if (usersWithoutPortrait.size() > 0) {
                                                        final List<String> userList = new ArrayList<>(usersWithoutPortrait);
                                                        //远程请求缺少的用户信息
                                                        remoteSource.getMomentUserData(userList,
                                                                new MomentRemoteSource.GetMomentUserDataCallback() {
                                                                    @Override
                                                                    public void onUserDataLoaded(List<MomentUserData> userDataList) {
                                                                        //将请求到的用户信息加入缓存
                                                                        for (MomentUserData userData : userDataList) {
                                                                            userDataMap.put(userData.getUserId(), userData);
                                                                        }
                                                                        //为本地获取的动态数据添加用户信息，并加入缓存
                                                                        for (MomentLocal momentLocal : moments) {
                                                                            Moment moment = MomentUtils.momentLocalToMoment(momentLocal);
                                                                            MomentUserData userData = userDataMap.get(moment.getUserId());
                                                                            moment.setUserData(userData.getNickName(), userData.getPortrait());
                                                                            if (moment.getCommentList() != null) {
                                                                                for (Comment comment : moment.getCommentList()) {
                                                                                    comment.setSenderData(userDataMap.get(comment.getSendId()));
                                                                                    comment.setRecvNickName(userDataMap.get(comment.getRecvId()).getNickName());
                                                                                }
                                                                            }
                                                                            momentMap.put(moment.getMomentId(), moment);
                                                                        }
                                                                        //获取最终的动态列表，返回
                                                                        callback.onMomentsLoaded(initMomentList(momentVersionList));
                                                                    }

                                                                    @Override
                                                                    public void onFailure(String error) {
                                                                        callback.onDataNotAvailable(error);
                                                                    }
                                                                });
                                                    } else {
                                                        callback.onMomentsLoaded(initMomentList(momentVersionList));
                                                    }
                                                }

                                                @Override
                                                public void onDataNotAvailable(String error) {
                                                    callback.onDataNotAvailable(error);
                                                }
                                            });
                                        }else {
                                            if (usersWithoutPortrait.size() > 0) {
                                                final List<String> userList = new ArrayList<>(usersWithoutPortrait);
                                                //远程请求缺少的用户信息
                                                remoteSource.getMomentUserData(userList,
                                                        new MomentRemoteSource.GetMomentUserDataCallback() {
                                                            @Override
                                                            public void onUserDataLoaded(List<MomentUserData> userDataList) {
                                                                //将请求到的用户信息加入缓存
                                                                for (MomentUserData userData : userDataList) {
                                                                    userDataMap.put(userData.getUserId(), userData);
                                                                }
                                                                //为本地获取的动态数据添加用户信息，并加入缓存
                                                                for (MomentLocal momentLocal : moments) {
                                                                    Moment moment = MomentUtils.momentLocalToMoment(momentLocal);
                                                                    MomentUserData userData = userDataMap.get(moment.getUserId());
                                                                    moment.setUserData(userData.getNickName(), userData.getPortrait());
                                                                    if (moment.getCommentList() != null) {
                                                                        for (Comment comment : moment.getCommentList()) {
                                                                            comment.setSenderData(userDataMap.get(comment.getSendId()));
                                                                            comment.setRecvNickName(userDataMap.get(comment.getRecvId()).getNickName());
                                                                        }
                                                                    }
                                                                    momentMap.put(moment.getMomentId(), moment);
                                                                }
                                                                //获取最终的动态列表，返回
                                                                callback.onMomentsLoaded(initMomentList(momentVersionList));
                                                            }

                                                            @Override
                                                            public void onFailure(String error) {
                                                                callback.onDataNotAvailable(error);
                                                            }
                                                        });
                                            } else {
                                                callback.onMomentsLoaded(initMomentList(momentVersionList));
                                            }
                                        }
                                    }
                                });*/
                                List<String> momentsNeedToRequest = new ArrayList<>();
                                for(MomentVersion version : momentWithoutCache){
                                    momentsNeedToRequest.add(version.getMomentId());
                                }
                                remoteSource.getMoments(momentsNeedToRequest, new MomentRemoteSource.GetMomentsCallback() {
                                    @Override
                                    public void onMomentsLoaded(List<Moment> momentList) {
                                        //将请求到的动态信息加入缓存
                                        for (Moment moment : momentList) {
                                            momentMap.put(moment.getMomentId(), moment);
                                        }
                                        callback.onMomentsLoaded(initMomentList(momentVersionList));
                                    }

                                    @Override
                                    public void onDataNotAvailable(String error) {
                                        callback.onDataNotAvailable(error);
                                    }
                                });
                            } else {
                                //获取最终的动态列表，返回
                                callback.onMomentsLoaded(initMomentList(momentVersionList));
                            }
                        } else {
                            //附近没有动态信息，返回空列表
                            callback.onMomentsLoaded(new ArrayList<Moment>());
                        }
                    }

                    @Override
                    public void onDataNotAvailable(String error) {
                        callback.onDataNotAvailable(error);
                    }
                });
    }

    @Override
    public void createMoment(String userId, String text, Location location, CreateMomentCallback callback) {
        createMoment(userId, text, location, null, callback);
    }

    @Override
    public void createMoment(String userId, String text, Location location, List<File> imageFiles,
                             final CreateMomentCallback callback) {
        //远程创建动态
        remoteSource.createMoment(userId, location, text, imageFiles,
                new MomentRemoteSource.CreateMomentCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onFailure(error);
                    }
                });
    }

    @Override
    public void createComment(String text, String momentId, String sendId, String receiverId,
                              final CreateCommentCallback callback) {
        //远程创建评论
        remoteSource.createComment(text, momentId, receiverId, sendId,
                new MomentRemoteSource.CreateCommentCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onFailure(error);
                    }
                });
    }

    @Override
    public void refreshMoment(String momentId) {
        //删除对应id的动态缓存
        momentMap.remove(momentId);
    }

    //根据动态版本信息列表，从本地缓存中获取动态信息列表
    private List<Moment> initMomentList(List<MomentVersion> momentVersionList){
        List<Moment> momentList = new ArrayList<>();
        for(MomentVersion momentVersion : momentVersionList){
            if(momentMap.containsKey(momentVersion.getMomentId())){
                momentList.add(momentMap.get(momentVersion.getMomentId()));
            }
        }
        return momentList;
    }

}
