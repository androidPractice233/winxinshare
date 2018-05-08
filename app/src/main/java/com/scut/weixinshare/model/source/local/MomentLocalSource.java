package com.scut.weixinshare.model.source.local;

import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.MomentVersion;

import java.util.List;

public interface MomentLocalSource {

    //监听获取多条动态回调接口
    interface GetMomentsCallback{

        void onMomentsLoaded(List<MomentLocal> moments);

    }

    //获取多条动态
    void getMoments(List<MomentVersion> momentVersions, GetMomentsCallback callback);

    //创建动态
    void createMoment(Moment moment);

    //创建多条动态
    void createMoments(List<Moment> moments);

}
