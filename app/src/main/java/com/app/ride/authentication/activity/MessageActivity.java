package com.app.ride.authentication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ride.R;
import com.app.ride.authentication.adapter.ChatListAdapter;
import com.app.ride.authentication.model.MessageModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.DateTimeUtil;
import com.app.ride.authentication.utility.Globals;
import com.app.ride.authentication.utility.PaginationProgressBarAdapter;
import com.app.ride.authentication.utility.VerticalSpaceChatItemDecoration;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    RecyclerView rvMessageList;
    AppCompatEditText etMessage;
    AppCompatTextView tvTitle;
    AppCompatImageView ivSend;
    LinearLayoutCompat liProgress;

    private ChatListAdapter adapter;

    private FirebaseUser firebaseUser;
    private String opponentId = "";
    private String receiverName = "";
    private String requestId = "";
    private String firebaseUserid;
    private String conversationKey = "";
    private DocumentSnapshot lastVisible;
    private long pagePerCount = 20L;
    private ArrayList<MessageModel> messagesArrayList;
    private LinearLayoutManager layoutManager;
    private boolean loading;
    private boolean hasLoaded;
    public static final int PAGE_COUNT_ITEMS = 20;
    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
        setUpRecycleView();
        loadFirstPageItems(PAGE_COUNT_ITEMS);
    }

    private void initView() {
        globals = new Globals();

        rvMessageList = findViewById(R.id.rvMessageList);
        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);
        tvTitle = findViewById(R.id.tvTitle);
//        liProgress = (LinearLayoutCompat) findViewById(R.id.liProgress);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //opponent Firebase id
        if (getIntent().hasExtra(Constant.FD_OPPONENT_UID)) {
            opponentId = getIntent().getStringExtra(Constant.FD_OPPONENT_UID);
        }

        //opponent name
        if (getIntent().hasExtra(Constant.RIDE_name)) {
            receiverName = getIntent().getStringExtra(Constant.RIDE_name);
            tvTitle.setText(receiverName);
        }
        if (getIntent().hasExtra(Constant.RIDE_REQUEST_ID)) {
            requestId = getIntent().getStringExtra(Constant.RIDE_REQUEST_ID);
        }
        if (getIntent().hasExtra(Constant.RIDE_CONVERSATION_KEY)) {
            conversationKey = getIntent().getStringExtra(Constant.RIDE_CONVERSATION_KEY);
        }

        if (conversationKey.isEmpty()) {
            conversationKey = generateConversationId(opponentId, globals.getFireBaseId());
            Log.e("TAG", "conversationKey: $conversationKey");
        }


        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText().toString().trim().length() > 0) {
                    sendMessage(etMessage.getText().toString());
                } else {
                    Toast.makeText(MessageActivity.this, "Please enter message", Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    private void setUpRecycleView() {
        ArrayList<MessageModel> list = new ArrayList<>();
        adapter = new ChatListAdapter(MessageActivity.this, list);
        layoutManager = new LinearLayoutManager(MessageActivity.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rvMessageList.setLayoutManager(layoutManager);
        rvMessageList.setHasFixedSize(true);
        rvMessageList.setAdapter(adapter);
        rvMessageList.addItemDecoration(new VerticalSpaceChatItemDecoration(15));
    }


    private void setPagination() {
        Paginate.with(rvMessageList, new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                if (!loading) {
                    loadNextMessages();
                }
            }

            @Override
            public boolean isLoading() {
                if(messagesArrayList.size()==0){
                    loading = true;
                }else {
                    loading = false;
                }
                return loading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return hasLoaded;
            }
        })
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new PaginationProgressBarAdapter())
               .build();

    }

    private void loadFirstPageItems(final Integer count) {
        if (adapter != null && count <= PAGE_COUNT_ITEMS) {
            adapter.cleanup();
        }
        globals.showHideProgress(MessageActivity.this, true);


        FirebaseFirestore.getInstance()
                .collection(Constant.RISE_CONVERSATION_TABLE).document(requestId)
                .collection(Constant.RISE_MESSAGE_TABLE)
                .orderBy(Constant.RISE_CREATED_AT, Query.Direction.DESCENDING).limit(pagePerCount).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                loading = false;

                globals.showHideProgress(MessageActivity.this, false);

                if (value != null && value.getDocuments().size() > 0) {
                    messagesArrayList = new ArrayList<MessageModel>();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        MessageModel message = snapshot.toObject(MessageModel.class);
                        messagesArrayList.add(message);
                    }
                    if(messagesArrayList.size()==pagePerCount){
                        setPagination();

                    }

                    if (value.getDocuments() != null) {
                        lastVisible = value.getDocuments().get((value.getDocuments().size()) - 1);

                        if (value.getDocuments().size() == pagePerCount) {
                            hasLoaded = false;
                        } else if (value.getDocuments().size() < pagePerCount) {
                            hasLoaded = true;

                        }
                    } else {
                        hasLoaded = true;
                    }


                    adapter.doRefresh(messagesArrayList);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rvMessageList.smoothScrollToPosition(0);
                        }
                    }, 3000);
                } else {
                    hasLoaded = true;
                }


            }
        });

//        progressbar.setVisibility(View.GONE);
    }


    private void loadNextMessages() {
        loading = true;
        if (lastVisible != null) {
            globals.showHideProgress(MessageActivity.this, true);
            Query query = FirebaseFirestore.getInstance()
                    .collection(Constant.RISE_CONVERSATION_TABLE)
                    .document(requestId)
                    .collection(Constant.RISE_MESSAGE_TABLE)
                    .orderBy(Constant.RISE_CREATED_AT, Query.Direction.DESCENDING).startAfter(lastVisible).limit(pagePerCount);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    loading = false;
                    globals.showHideProgress(MessageActivity.this, false);
                    if (value != null) {
                        messagesArrayList = new ArrayList<MessageModel>();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            MessageModel message = snapshot.toObject(MessageModel.class);
                            messagesArrayList.add(message);
                        }
                        if (value.getDocuments() != null) {
                            lastVisible = value.getDocuments().get((value.getDocuments().size()) - 1);

                            if (value.getDocuments().size() == pagePerCount) {
                                hasLoaded = false;
                            } else if (value.getDocuments().size() < pagePerCount) {
                                hasLoaded = true;

                            }
                        } else {
                            hasLoaded = true;
                        }
                    } else {
                        hasLoaded = true;
                    }
                    adapter.doRefresh(messagesArrayList);
                }
            });


//  progressbar.setVisibility(View.GONE);

        }
    }

    private void sendMessage(String message) {
        MessageModel chatData = new MessageModel(message.trim(), globals.getFireBaseId(), opponentId, false, 1,
                new DateTimeUtil().getCurrentUTCTimeStampForChat());

        ArrayList<String> userIds = new ArrayList<>();
        userIds.add(opponentId);
        userIds.add(globals.getFireBaseId());

        HashMap<String, Object> mapConversation = new HashMap<String, Object>();
        mapConversation.put(Constant.RIDE_UPDATED_AT, String.valueOf(new DateTimeUtil().getCurrentUTCTimeStampForChat()));
        if (receiverName == null) {
            receiverName = "";
        }
        mapConversation.put(Constant.RIDE_USER_NAME, receiverName);
        mapConversation.put(Constant.RIDE_USER_ID, userIds);
        mapConversation.put(Constant.RIDE_LAST_MESSAGE, message);
        mapConversation.put(Constant.RIDE_CONVERSATION_KEY, conversationKey);
        mapConversation.put(Constant.RIDE_REQUEST_ID, requestId);

        // Set Last Conversation
        FirebaseFirestore.getInstance().collection(Constant.RISE_CONVERSATION_TABLE)
                .document(requestId).set(mapConversation, SetOptions.merge());

        //Send message
        FirebaseFirestore.getInstance().collection(Constant.RISE_CONVERSATION_TABLE)
                .document(requestId)
                .collection(Constant.RISE_MESSAGE_TABLE)
                .add(chatData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                rvMessageList.scrollToPosition(0);
                etMessage.setText("");
            }
        });
    }

    private String generateConversationId(String userId1, String userId2) {
        String key = "";
        if (userId1.compareTo(userId2) > 0) {
            key = userId1 + "_" + userId2;
        } else {
            key = userId2 + "_" + userId1;
        }
        return key;
    }

}


