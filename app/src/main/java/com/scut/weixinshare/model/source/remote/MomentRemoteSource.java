package com.scut.weixinshare.model.source.remote;

import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.MomentUserData;
import com.scut.weixinshare.model.source.MomentVersion;

import java.io.File;
import java.util.List;

//动态数据远程来源接口
public interface MomentRemoteSource {

    //监听获取附近动态接口
    interface GetNearbyMomentsCallback{

        void onMomentVersionsLoaded(List<MomentVersion> momentVersionList);

        void onDataNotAvailable(String error);

    }
    interface GetPersonMomentsCallback{

        void onMomentVersionsLoaded(List<MomentVersion> momentVersionList);

        void onDataNotAvailable(String error);

    }

    //监听获取单条动态接口
    interface GetMomentCallback{

        void onMomentLoaded(Moment moment);

        void onDataNotAvailable(String error);

    }

    //监听获取多条动态接口
    interface GetMomentsCallback{

        void onMomentsLoaded(List<Moment> momentList);

        void onDataNotAvailable(String error);

    }

    //监听创建动态接口
    interface CreateMomentCallback{

        void onSuccess();

        void onFailure(String error);

    }

    //监听创建评论接口
    interface CreateCommentCallback{

        void onSuccess();

        void onFailure(String error);

    }

    //监听获取动态中的用户信息接口
    interface GetMomentUserDataCallback{

        void onUserDataLoaded(List<MomentUserData> userDataList);

        void onFailure(String error);
    }

    //获取附近动态的版本信息
    void getNearbyMoments(Location location, int pageNum, int pageSize,
                          GetNearbyMomentsCallback callback);

    //获取单条动态详细
    void getMoment(String momentId, GetMomentCallback callback);
//
    void getSomebodyMoments(String personId,   int pageNum, int pageSize, final  GetPersonMomentsCallback callback);

    //获取多条动态详细
    void getMoments(List<String> momentIds, GetMomentsCallback callback);

    //创建动态
    void createMoment(Location location, String text, List<File> imageFiles,
                      CreateMomentCallback callback);

    //创建评论
    void createComment(String text, String momentId, String receiverId,
                       CreateCommentCallback callback);

    //获取动态中的用户信息（昵称与头像uri）
    void getMomentUserData(List<String> userId, GetMomentUserDataCallback callback);

}
