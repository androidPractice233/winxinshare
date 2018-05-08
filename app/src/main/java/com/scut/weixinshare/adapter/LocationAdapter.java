package com.scut.weixinshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scut.weixinshare.R;
import com.scut.weixinshare.model.Location;

import java.util.List;

//定位选择界面recyclerView对应的adapter
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Location> locationList;       //定位数据列表
    private LocationItemListener listener;     //itemView监听器

    public LocationAdapter(List<Location> locationList, LocationItemListener listener){
        this.locationList = locationList;
        this.listener = listener;
    }

    public void setAdapterData(List<Location> locationList){
        this.locationList = locationList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView name;
        TextView address;

        ViewHolder(View itemView){
            super(itemView);
            this.view = itemView;
            name = itemView.findViewById(R.id.location_name);
            address = itemView.findViewById(R.id.location_address);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Location location = locationList.get(position);
        holder.name.setText(location.getName());
        holder.address.setText(location.getAddress());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(location);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public interface LocationItemListener{

        void onClick(Location location);

    }

}
