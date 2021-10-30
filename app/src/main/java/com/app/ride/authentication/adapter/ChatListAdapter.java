package com.app.ride.authentication.adapter;

import static com.app.ride.authentication.utility.MessageEnum.RECEIVER;
import static com.app.ride.authentication.utility.MessageEnum.SENDER;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ride.R;
import com.app.ride.authentication.model.MessageModel;
import com.app.ride.authentication.utility.DateTimeUtil;
import com.app.ride.authentication.utility.Globals;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MessageModel> dataList = new ArrayList<>();
    private Context context;
    Globals globals;


    public ChatListAdapter(Context context, ArrayList<MessageModel> listData) {
        globals = new Globals();
        this.dataList = listData;
        this.context = context;
    }
    public void cleanup() {
        if (dataList != null) {
            dataList.clear();
        }
        notifyDataSetChanged();
    }
    public void doRefresh(ArrayList<MessageModel>  chatMessages) {
        this.dataList.clear();
        this.dataList.addAll(chatMessages);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SENDER.value()) {
           return new LeftMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, parent, false));
        } else {
            return new RightMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, parent, false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder.getItemViewType() == SENDER.value()) {
                LeftMessageHolder leftMessageHolder= (LeftMessageHolder) holder;
                leftMessageHolder.setDataToView(dataList.get(position));
            } else {
                RightMessageHolder rightMessageHolder= (RightMessageHolder) holder;
                rightMessageHolder.setDataToView(dataList.get(position));
            }

        } catch (Exception e) {
            Log.d("mn13exception", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getSenderId().equals(globals.getFireBaseId())) {
            return SENDER.value();
        } else {
            return RECEIVER.value();

        }
    }


    private class LeftMessageHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tvMessage, tvTime, tvHeaderText;
        public LeftMessageHolder(View inflate) {
            super(inflate);
            tvMessage = inflate.findViewById(R.id.tvMessage);
            tvTime = inflate.findViewById(R.id.tvTime);
            tvHeaderText = inflate.findViewById(R.id.tvHeaderText);
        }

        public void setDataToView(MessageModel messageModel) {
            tvMessage.setText(messageModel.getMessage());
            tvTime.setText( new DateTimeUtil().convertUtcTimeToLocalTimeFormatForChat(messageModel.getCreatedAt()));
            tvHeaderText.setText(new DateTimeUtil().convertUTCTToLocalDate(messageModel.getCreatedAt(), context, false));
            headerVisibility(tvHeaderText, getAdapterPosition(), messageModel);
        }
    }

    private class RightMessageHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvMessage, tvTime, tvHeaderText;

        public RightMessageHolder(View inflate) {
            super(inflate);
            tvMessage = inflate.findViewById(R.id.tvMessage);
            tvTime = inflate.findViewById(R.id.tvTime);
            tvHeaderText = inflate.findViewById(R.id.tvHeaderText);
        }

        public void setDataToView(MessageModel MessageModel) {
            tvMessage.setText(MessageModel.getMessage());
            tvTime.setText( new DateTimeUtil().convertUtcTimeToLocalTimeFormatForChat(MessageModel.getCreatedAt()));
            tvHeaderText.setText(new DateTimeUtil().convertUTCTToLocalDate(MessageModel.getCreatedAt(), context, false));
            headerVisibility(tvHeaderText, getAdapterPosition(), MessageModel);
        }
    }

    private void headerVisibility(AppCompatTextView tvHeaderText, int adapterPosition, MessageModel messageModel) {

        if (adapterPosition < dataList.size() - 1) {
            if (!new DateTimeUtil().convertUTCTToLocalDate(messageModel.getCreatedAt(), context, false).equals(
                    new DateTimeUtil().convertUTCTToLocalDate(dataList.get(adapterPosition + 1).getCreatedAt(), context, false) )) {
                tvHeaderText.setVisibility(View.VISIBLE);
            } else {
                tvHeaderText.setVisibility(View.GONE);
            }
        } else {
            tvHeaderText.setVisibility(View.VISIBLE);
        }
    }
}

