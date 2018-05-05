package com.scut.weixinshare.contract;

import com.scut.weixinshare.BasePresenter;
import com.scut.weixinshare.BaseView;
import com.scut.weixinshare.model.Location;

import java.util.List;

public interface PickLocationContract {

    interface View extends BaseView<Presenter>{

        void showReminderMessage(String message);

        void setView(List<Location> locationList);

    }

    interface Presenter extends BasePresenter{

    }

}
