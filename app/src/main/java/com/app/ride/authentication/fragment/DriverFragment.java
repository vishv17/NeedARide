package com.app.ride.authentication.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ride.R;
import com.app.ride.authentication.activity.DashboardActivity;
import com.app.ride.authentication.activity.DriverRideActivity;
import com.app.ride.authentication.adapter.DriverRideListAdapter;
import com.app.ride.authentication.model.DriverRequestModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DriverFragment extends Fragment {

    RecyclerView rvDriverList;
    ArrayList<DriverRequestModel> dataList;
    Context context;
    Globals globals;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        globals = new Globals();
        rvDriverList = view.findViewById(R.id.rvDriverList);
        getDataFromDatabase();
    }

    private void getDataFromDatabase() {
        globals.showHideProgress((Activity) context, true);

        FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                globals.showHideProgress((Activity) context, false);
                if (value != null) {
                    if (value.isEmpty()) {

                        return;
                    } else {
                        List<DriverRequestModel> downloadInfoList = value.toObjects(DriverRequestModel.class);
                        // Add all to your list
                        dataList = new ArrayList<DriverRequestModel>();
                        for (int i = 0; i < downloadInfoList.size(); i++) {
                            dataList.add(downloadInfoList.get(i));
                        }

//                    dataList.addAll(downloadInfoList);
                        DriverRideListAdapter adapter = new DriverRideListAdapter(context, dataList, new DriverRideListAdapter.OnViewClick() {
                            @Override
                            public void onEditClick(DriverRequestModel model) {
                                Intent intent = new Intent(context, DriverRideActivity.class);
                                intent.putExtra("DATA", model);
                                startActivity(intent);
                            }
                        });
                        rvDriverList.setHasFixedSize(true);
                        rvDriverList.setLayoutManager(new LinearLayoutManager(context));
                        rvDriverList.setAdapter(adapter);
                        globals.showHideProgress((Activity) context, false);

                    }
                }
            }
        });
    }


}