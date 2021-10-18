package com.app.ride.authentication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ride.R;
import com.app.ride.authentication.model.PassengerRequestModel;

import java.util.ArrayList;

public class PassengerRideListAdapter extends RecyclerView.Adapter<PassengerRideListAdapter.ViewHolder> {
    private ArrayList<PassengerRequestModel> dataList;


    public PassengerRideListAdapter(ArrayList<PassengerRequestModel> listData) {
        this.dataList = listData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }


    @NonNull
    @Override
    public PassengerRideListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_driver_ride, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerRideListAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(dataList.get(position).getStartPlace()+" "+dataList.get(position).getEndPlace());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

