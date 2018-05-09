package com.scut.weixinshare.presenter;

import com.scut.weixinshare.contract.HomeContract;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.model.Moment;
import com.scut.weixinshare.model.source.LocationDataSource;
import com.scut.weixinshare.model.source.MomentDataSource;

import java.util.List;

/**
 * Created by skyluo on 2018/5/8.
 */

public class PersonHomePresenter extends HomePresenter{

    public PersonHomePresenter(HomeContract.View view, MomentDataSource momentDataSource, LocationDataSource locationDataSource) {
        super(view, momentDataSource, locationDataSource);
    }

    @Override
    public void requestNewMoments() {
        isLoading = true;
                /*MomentsRepository.getInstance().getMoments(location, 0, PAGE_SIZE,
                        new MomentDataSource.GetMomentsCallback(){
                    @Override
                    public void onMomentsLoaded(List<Moment> momentList) {
                        view.initMoments(momentList);
                        if(momentList.size() < PAGE_SIZE){
                            view.setListEndView();
                        }
                        pageNum = 0;
                        isLoading = false;
                        view.hideRefreshing();
                    }

                    @Override
                    public void onDataNotAvailable(String error) {
                        view.showReminderMessage(error);
                        isLoading = false;
                        view.hideRefreshing();
                    }
                });*/
                view.initMoments(initTestMoments());
                if(isFirst){
                    isFirst = false;
                    view.showMomentList();
                }
                isLoading = false;
                view.hideRefreshing();
                state = LOADING;
                view.setListLoadingView();



        }


    @Override
    public void requestNextMoments() {
        if(count == 5){
            view.setListEndView();
            state = END;
        } else if(!isLoading && state == LOADING){
            isLoading = true;
            if(count == 2){
                view.setListErrorView();
                state = NETWORK_ERROR;
            } else {
                view.setListLoadingView();
                List<Moment> addedMoments = initTestMoments();
                //momentList.addAll(addedMoments);
                view.addMoments(addedMoments);
            }
            ++count;
            isLoading = false;
        }
    }
}
