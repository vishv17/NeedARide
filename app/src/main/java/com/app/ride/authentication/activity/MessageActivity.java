package com.app.ride.authentication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    RecyclerView rvMessageList;
    AppCompatEditText etMessage;
    AppCompatTextView tvTitle;
    AppCompatImageView ivSend;
    ProgressBar progressbar;

    private ChatListAdapter adapter;

    private FirebaseUser firebaseUser;
    private String opponentId = "";
    private String receiverName = "";
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
        setPagination();
        loadFirstPageItems(PAGE_COUNT_ITEMS);
    }

    private void initView() {
        globals = new Globals();

        rvMessageList = findViewById(R.id.rvMessageList);
        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);
        tvTitle = findViewById(R.id.tvTitle);
        progressbar = findViewById(R.id.progressbar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUserid = firebaseUser.getUid();
//        firebaseUserid = "fzuHO1EzsBSO10qQR0F17Jzlbcm2";
//        opponentId = "rvcFda6QMvOH4Gsw8MS83Qq6d9e2";

        //opponent Firebase id
        if (getIntent().hasExtra(Constant.FD_OPPONENT_UID)) {
            opponentId = getIntent().getStringExtra(Constant.FD_OPPONENT_UID);
        }

        //opponent name
        if (getIntent().hasExtra(Constant.RIDE_name)) {
            receiverName = getIntent().getStringExtra(Constant.RIDE_name);
            tvTitle.setText(receiverName);
        }

        if (conversationKey.isEmpty()) {
            conversationKey = generateConversationId(opponentId, firebaseUserid);
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
                return loading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return hasLoaded;
            }
        })
                .setLoadingTriggerThreshold(1)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new PaginationProgressBarAdapter())
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return 1;
                    }
                }).build();

    }

    private void loadFirstPageItems(final Integer count) {
        if (adapter != null && count <= PAGE_COUNT_ITEMS) {
            adapter.cleanup();
        }
        progressbar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance()
                .collection(Constant.RISE_CONVERSATION_TABLE).document(conversationKey)
                .collection(Constant.RISE_MESSAGE_TABLE)
                .orderBy(Constant.RISE_CREATED_AT, Query.Direction.DESCENDING).limit(pagePerCount).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                loading = false;
                if (value != null && value.getDocuments().size() > 0) {
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

        progressbar.setVisibility(View.GONE);
    }


    private void loadNextMessages() {
        loading = true;
        if (lastVisible != null) {
            Query query = FirebaseFirestore.getInstance()
                    .collection(Constant.RISE_CONVERSATION_TABLE)
                    .document(conversationKey)
                    .collection(Constant.RISE_MESSAGE_TABLE)
                    .orderBy(Constant.RISE_CREATED_AT, Query.Direction.DESCENDING).startAfter(lastVisible).limit(pagePerCount);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    loading = false;
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
        }
    }

    private void sendMessage(String message) {
        MessageModel chatData = new MessageModel(message.trim(), firebaseUserid, opponentId, false, 1,
                new DateTimeUtil().getCurrentUTCTimeStampForChat());
        //Send message
        FirebaseFirestore.getInstance().collection(Constant.RISE_CONVERSATION_TABLE)
                .document(conversationKey)
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


