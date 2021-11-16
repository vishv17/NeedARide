package com.app.ride.authentication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ride.R;
import com.app.ride.authentication.model.ChatListModel;

import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private ArrayList<ChatListModel> dataList;
    private Context context;
    private OnViewClick listener;


    public MessageListAdapter(Context context, ArrayList<ChatListModel> listData, OnViewClick listener) {
        this.dataList = listData;
        this.context = context;
        this.listener = listener;
    }

    public void doRefresh(ArrayList<ChatListModel> list) {
        this.dataList.clear();
        this.dataList.addAll(list);
        notifyDataSetChanged();

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (AppCompatTextView)itemView.findViewById(R.id.tvName);

        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_dialog, parent, false));
    }

    public interface OnViewClick {
        void onEditClick(ChatListModel model);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatListModel model = dataList.get(position);
        holder.tvName.setText(model.getRequestId().toString());
        holder.tvName.setOnClickListener(new View.OnClickListener() {
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

