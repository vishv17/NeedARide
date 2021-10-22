package com.app.ride.authentication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ride.R;
import com.app.ride.authentication.model.DriverRequestModel;
import com.app.ride.authentication.model.PassengerRequestModel;

import java.util.ArrayList;

public class PassengerRideListAdapter extends RecyclerView.Adapter<PassengerRideListAdapter.ViewHolder> {
    private ArrayList<PassengerRequestModel> dataList;
    private Context context;
    private  OnViewClick listener;


    public PassengerRideListAdapter(Context context, ArrayList<PassengerRequestModel> listData, OnViewClick listener) {
        this.dataList = listData;
        this.context = context;
        this.listener = listener;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvDateOfJourney,tvStartPlace,tvEndPlace,tvEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateOfJourney = itemView.findViewById(R.id.tvDateOfJourney);
            tvStartPlace= itemView.findViewById(R.id.tvStartPlace);
            tvEndPlace= itemView.findViewById(R.id.tvEndPlace);
            tvEdit= itemView.findViewById(R.id.tvEdit);
        }
    }

    public interface OnViewClick{
        void onEditClick(PassengerRequestModel model);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_passenger_ride, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PassengerRequestModel model = dataList.get(position);
        holder.tvDateOfJourney.setText(context.getString(R.string.text_dis_date)+" - "+model.getDateOfJourney());
        holder.tvStartPlace.setText(context.getString(R.string.text_start__dis_place)+" - "+model.getStartPlace());
        holder.tvEndPlace.setText(context.getString(R.string.text_end_dis_place)+" - "+model.getEndPlace());
        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditClick(model);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

