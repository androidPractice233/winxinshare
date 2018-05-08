package com.scut.weixinshare.model.source;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MomentsRepository implements MomentDataSource {

    private static MomentsRepository INSTANCE;

    private Map<String, Moment> momentMap;
    private Map<String, MomentUserData> userDataMap;
    private MomentLocalSource localSource;
    private MomentRemoteSource remoteSource;

    private MomentsRepository(MomentLocalSource localSource, MomentRemoteSource remoteSource){
        this.localSource = localSource;
        this.remoteSource = remoteSource;
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
    public void getMoment(String momentId, final GetMomentCallback callback) {
        if(momentMap.containsKey(momentId)){
            callback.onMomentLoaded(momentMap.get(momentId));
        } else {
            remoteSource.getMoment(momentId, new MomentRemoteSource.GetMomentCallback() {
                @Override
                public void onMomentLoaded(Moment moment) {
                    momentMap.put(moment.getMomentId(), moment);
                    localSource.createMoment(moment);
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
        remoteSource.getNearbyMoments(location, pageNum, pageSize,
                new MomentRemoteSource.GetNearbyMomentsCallback() {
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
    public void createMoment(String text, Location location, CreateMomentCallback callback) {
        createMoment(text, location, null, callback);
    }

    @Override
    public void createMoment(final String text, final Location location, final List<File> imageFiles,
                             final CreateMomentCallback callback) {
        remoteSource.createMoment(location, text, imageFiles,
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
    public void createComment(String text, String momentId, String senderId,
                              String receiverId, final CreateCommentCallback callback) {
        remoteSource.createComment(text, momentId, senderId, receiverId,
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
        momentMap.remove(momentId);
    }

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
