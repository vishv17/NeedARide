package com.app.ride.authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.adapter.RideListAdapter;
import com.app.ride.authentication.model.DriverRequestModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideListActivity extends AppCompatActivity implements RideListAdapter.onClickListener, View.OnClickListener {

    private RideListActivity activity;
    private RecyclerView rvRideList;
    private AppCompatTextView txtErrorText;
    private String userId;
    private ArrayList<DriverRequestModel> driverRequestList;
    private RideListAdapter rideListAdapter;
    private Globals globals;
    private AppCompatImageView ivBack;
    private static final String TAG = "RideListActivity";
    private String passengerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_list);
        activity = this;

        initView();
        initViewAction();
        initViewListener();
    }

    private void initView() {
        globals = new Globals();
        driverRequestList = new ArrayList<>();
        rvRideList = findViewById(R.id.rvRideList);
        txtErrorText = findViewById(R.id.txtErrorText);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            if (getIntent().getStringExtra(Constant.RIDE_Firebase_Uid) != null) {
                userId = getIntent().getStringExtra(Constant.RIDE_Firebase_Uid);
            }

            if (getIntent().getStringExtra(Constant.RIDE_passenger_Uid) != null) {
                passengerId = getIntent().getStringExtra(Constant.RIDE_passenger_Uid);
            }
        }

        Log.e(TAG, "initView: userID-->" + userId);
        if (userId != null) {
            FirebaseFirestore.getInstance()
                    .collection(Constant.RIDE_Driver_request)
                    .whereEqualTo(Constant.RIDE_Firebase_Uid, userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (driverRequestList != null) {
                                    driverRequestList.clear();
                                    List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                                    for (DocumentSnapshot snapshot : documentSnapshotList) {
                                        DriverRequestModel driverRequestModel = snapshot.toObject(DriverRequestModel.class);
                                        driverRequestList.add(driverRequestModel);
                                    }
                                    setAdapter(driverRequestList);
                                } else {
                                    Toast.makeText(activity, "No Data Found", Toast.LENGTH_SHORT).show();
                                    rvRideList.setVisibility(View.GONE);
                                    txtErrorText.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void setAdapter(ArrayList<DriverRequestModel> driverRequestList) {
        if (driverRequestList != null) {
            if (driverRequestList.size() > 0) {
                rvRideList.setVisibility(View.VISIBLE);
                txtErrorText.setVisibility(View.GONE);
                if (rideListAdapter == null) {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                    rvRideList.setLayoutManager(linearLayoutManager);
                    rideListAdapter = new RideListAdapter(activity, driverRequestList);
                    rideListAdapter.registerListener(activity);
                    rvRideList.setAdapter(rideListAdapter);
                } else {
                    rideListAdapter.doRefresh(driverRequestList);
                }
            } else {
                rvRideList.setVisibility(View.GONE);
                txtErrorText.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initViewAction() {

    }

    private void initViewListener() {
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onRideCardClick(int pos, DriverRequestModel driverRequestModel) {
        if (driverRequestModel.getSeatAvailable() > 0) {
            globals.showHideProgress(activity, true);
            Map<String, Object> data = new HashMap<>();
            ArrayList<String> acceptedUserList = new ArrayList<>();
            ArrayList<String> fcmList = new ArrayList<>();
            acceptedUserList.add(globals.getFireBaseId());
            fcmList.add(globals.getFCMToken(activity));
            data.put(Constant.ACCEPTED_USER,acceptedUserList);
            data.put("acceptedId",fcmList);
            data.put(Constant.RIDE_seat_available, (driverRequestModel.getSeatAvailable() - 1));
            FirebaseFirestore.getInstance()
                    .collection(Constant.RIDE_Driver_request)
                    .whereEqualTo(Constant.RIDE_driver_Uid, driverRequestModel.getDriverId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                DriverRequestModel model = documentSnapshot.toObject(DriverRequestModel.class);
                                FirebaseFirestore.getInstance()
                                        .collection(Constant.RIDE_Driver_request)
                                        .document(documentSnapshot.getId())
                                        .update(data)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseFirestore.getInstance()
                                                            .collection(Constant.RIDE_passenger_request)
                                                            .whereEqualTo(Constant.RIDE_passenger_Uid, passengerId)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot documentSnapshot1 = task.getResult().getDocuments().get(0);
                                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                                        hashMap.put(Constant.RIDE_ID, documentSnapshot.getId());
                                                                        FirebaseFirestore.getInstance()
                                                                                .collection(Constant.RIDE_passenger_request)
                                                                                .document(documentSnapshot1.getId())
                                                                                .update(hashMap)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            globals.showHideProgress(activity, false);
                                                                                            Toast.makeText(activity, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                                                                                            finish();
                                                                                        } else {
                                                                                            globals.showHideProgress(activity, false);
                                                                                            Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        globals.showHideProgress(activity, false);
                                                                        Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    globals.showHideProgress(activity, false);
                                                    Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                globals.showHideProgress(activity, false);
                                Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(activity, "Seat is not available, Please select another request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }
}