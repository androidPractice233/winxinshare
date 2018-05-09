package com.scut.weixinshare.model.source;

import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;

import java.io.File;
import java.util.List;

//动态信息来源接口
public interface MomentDataSource {

    //获取单条动态监听
    interface GetMomentCallback{

        void onMomentLoaded(Moment moment);

        void onDataNotAvailable(String error);

    }

    //获取多条动态监听
    interface GetMomentsCallback{

        void onMomentsLoaded(List<Moment> momentList);

        void onDataNotAvailable(String error);

    }

    //创建动态监听
    interface CreateMomentCallback{

        void onSuccess();

        void onFailure(String error);

    }

    //创建评论监听
    interface CreateCommentCallback{

        void onSuccess();

        void onFailure(String error);

    }

    //获取单条动态
    void getMoment(String momentId, GetMomentCallback callback);

    //获取动态列表
    void getMoments(Location location, int pageNum, int pageSize, GetMomentsCallback callback);

    //获取个人主页动态
    void getSomebodyMoments(String id, int pageNum, int pageSize, GetMomentsCallback callback);
    //创建不带图片的动态
    void createMoment(String text, Location location, CreateMomentCallback callback);

    //创建带图片的动态
    void createMoment(String text, Location location, List<File> imageFiles,
                      CreateMomentCallback callback);

    //创建评论
    void createComment(String text, String momentId, String senderId, String receiverId,
                       CreateCommentCallback callback);

    //清除单条动态缓存
    void refreshMoment(String momentId);

}
