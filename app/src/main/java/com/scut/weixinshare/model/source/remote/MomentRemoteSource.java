package com.scut.weixinshare.model.source.remote;

import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.MomentUserData;
import com.scut.weixinshare.model.source.MomentVersion;

import java.io.File;
import java.util.List;

public interface MomentRemoteSource {

    interface GetNearbyMomentsCallback{

        void onMomentVersionsLoaded(List<MomentVersion> momentVersionList);

        void onDataNotAvailable(String error);

    }
    interface GetPersonMomentsCallback{

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

    void getNearbyMoments(Location location, int pageNum, int pageSize,
                          GetNearbyMomentsCallback callback);

    void getMoment(String momentId, GetMomentCallback callback);

    void getSomebodyMoments(String personId,   int pageNum, int pageSize, final  GetPersonMomentsCallback callback);

    void getMoments(List<String> momentIds, GetMomentsCallback callback);

    void createMoment(Location location, String text, List<File> imageFiles,
                      CreateMomentCallback callback);

    void createComment(String text, String momentId, String senderId, String receiverId,
                       CreateCommentCallback callback);

    void getMomentUserData(List<String> userId, GetMomentUserDataCallback callback);

}
