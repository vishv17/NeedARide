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

import java.util.ArrayList;

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.RideViewHolder>
{

    private Context context;
    private ArrayList<DriverRequestModel> driverRequestList;
    private onClickListener listener;

    public RideListAdapter(Context context, ArrayList<DriverRequestModel> driverRequestList) {
        this.context = context;
        this.driverRequestList = driverRequestList;
    }

    public void registerListener(onClickListener listener)
    {
        this.listener = listener;
    }

    public void doRefresh(ArrayList<DriverRequestModel> driverRequestList)
    {
        this.driverRequestList = driverRequestList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride_list,parent,false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();

        DriverRequestModel driverRequestModel = driverRequestList.get(pos);
        holder.txtDriverName.setText("Driver Name : "+String.valueOf(driverRequestModel.getName()));
        holder.txtAvailableSeat.setText("Available Seat : "+String.valueOf(driverRequestModel.getSeatAvailable()));
        holder.txtStartPlace.setText("Start Place : " + driverRequestModel.getStartPlace());
        holder.txtEndPlace.setText("End Place : "+driverRequestModel.getEndPlace());
        holder.txtPrice.setText("Price : "+"$ "+String.valueOf(driverRequestModel.getCostPerSeat()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                {
                    listener.onRideCardClick(pos,driverRequestModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return driverRequestList.size();
    }

    public class RideViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView txtStartPlace,txtEndPlace,txtAvailableSeat,txtPrice,txtDriverName;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            txtStartPlace = itemView.findViewById(R.id.txtStartPlace);
            txtEndPlace = itemView.findViewById(R.id.txtEndPlace);
            txtAvailableSeat = itemView.findViewById(R.id.txtAvailableSeat);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDriverName = itemView.findViewById(R.id.txtDriverName);
        }
    }

    public interface onClickListener
    {
        void onRideCardClick(int pos,DriverRequestModel driverRequestModel);
    }
}
