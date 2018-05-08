package com.scut.weixinshare.model.source.remote;

import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.MomentUserData;
import com.scut.weixinshare.model.source.MomentVersion;

import java.io.File;
import java.util.List;

//动态数据远程来源接口
public interface MomentRemoteSource {

    interface GetNearbyMomentsCallback{

        void onMomentVersionsLoaded(List<MomentVersion> momentVersionList);

        void onDataNotAvailable(String error);

    }

    interface GetMomentCallback{

        void onMomentLoaded(Moment moment);

        void onDataNotAvailable(String error);

    }

    interface GetMomentsCallback{

        void onMomentsLoaded(List<Moment> momentList);

        void onDataNotAvailable(String error);

    }

    interface CreateMomentCallback{

        void onSuccess();

        void onFailure(String error);

    }

    interface CreateCommentCallback{

        void onSuccess();

        void onFailure(String error);

    }

    interface GetMomentUserDataCallback{

        void onUserDataLoaded(List<MomentUserData> userDataList);

        void onFailure(String error);
    }

    //获取附近动态的版本信息
    void getNearbyMoments(Location location, int pageNum, int pageSize,
                          GetNearbyMomentsCallback callback);

    //获取单条动态详细
    void getMoment(String momentId, GetMomentCallback callback);

    //获取多条动态详细
    void getMoments(List<String> momentIds, GetMomentsCallback callback);

    //创建动态
    void createMoment(Location location, String text, List<File> imageFiles,
                      CreateMomentCallback callback);

    //创建评论
    void createComment(String text, String momentId, String senderId, String receiverId,
                       CreateCommentCallback callback);

    //获取动态中的用户信息（昵称与头像uri）
    void getMomentUserData(List<String> userId, GetMomentUserDataCallback callback);

}
