package com.app.ride.authentication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ride.R;
import com.app.ride.authentication.adapter.MessageListAdapter;
import com.app.ride.authentication.model.ChatListModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.app.ride.authentication.utility.VerticalSpaceChatItemDecoration;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageListActivity extends AppCompatActivity implements MessageListAdapter.OnViewClick {
    RecyclerView rvMessageList;
    MessageListAdapter adapter;
    AppCompatTextView tvNoMsg;
    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        initView();
    }

    private void initView() {
        globals = new Globals();

        rvMessageList = findViewById(R.id.rvMessageList);
        tvNoMsg = findViewById(R.id.tvNoMsg);
        getFirebaseData();

    }

    private void getFirebaseData() {
        FirebaseFirestore.getInstance().collection(Constant.RISE_CONVERSATION_TABLE)
                .whereArrayContains(Constant.RIDE_USER_ID, globals.getFireBaseId())
                .orderBy(Constant.RIDE_UPDATED_AT, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        ArrayList<ChatListModel> list = new ArrayList<ChatListModel>();
                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                ChatListModel message = snapshot.toObject(ChatListModel.class);
                                list.add(message);
                            }
                            if (list.size() > 0) {
                                tvNoMsg.setVisibility(View.GONE);
                                rvMessageList.setVisibility(View.VISIBLE);
                                setAdapter(list);
                            } else {
                                tvNoMsg.setVisibility(View.VISIBLE);
                                rvMessageList.setVisibility(View.GONE);
                            }

                        }

                    }
                });
    }

    private void setAdapter(ArrayList<ChatListModel> list) {
        if (rvMessageList.getAdapter() == null) {
            adapter = new MessageListAdapter(this, list, this);
            rvMessageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvMessageList.addItemDecoration(new VerticalSpaceChatItemDecoration(20));
            rvMessageList.setAdapter(adapter);
        } else {
            adapter.doRefresh(list);
        }

    }

    @Override
    public void onEditClick(ChatListModel model) {
        String otherId = "";
        String otherName = "";
        Intent intent = new Intent(MessageListActivity.this, MessageActivity.class);
        String[] data = model.getConversationKey().split("_");
        for (String datum : data) {
            if (!(datum.equals(globals.getFireBaseId()))) {
                otherId = datum;
            }
        }

        intent.putExtra(Constant.FD_OPPONENT_UID, otherId);
        intent.putExtra(Constant.RIDE_CONVERSATION_KEY, model.getConversationKey());
        intent.putExtra(Constant.RIDE_name, model.getUsername());
        intent.putExtra(Constant.RIDE_REQUEST_ID, model.getRequestId());
        startActivity(intent);
    }
}