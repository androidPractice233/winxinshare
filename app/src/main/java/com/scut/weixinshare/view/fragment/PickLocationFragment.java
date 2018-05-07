package com.scut.weixinshare.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scut.weixinshare.R;
import com.scut.weixinshare.adapter.LocationAdapter;
import com.scut.weixinshare.contract.PickLocationContract;
import com.scut.weixinshare.model.Location;
import com.scut.weixinshare.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class PickLocationFragment extends Fragment implements PickLocationContract.View,
        LocationAdapter.LocationItemListener {

    private PickLocationContract.Presenter presenter;
    private LocationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_location, container,
                false);
        RecyclerView recyclerView = view.findViewById(R.id.list_locations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new LocationAdapter(new ArrayList<Location>(), this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null){
            presenter.start();
        }
    }

    @Override
    public void setPresenter(PickLocationContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showReminderMessage(String message) {
        ToastUtils.showToast(getContext(), message);
    }

    @Override
    public void setView(List<Location> locationList) {
        adapter.setAdapterData(locationList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(Location location) {
        Intent intent = new Intent();
        intent.putExtra("location", location);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
