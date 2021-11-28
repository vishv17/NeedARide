package com.app.ride.authentication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.app.ride.R;

public class RideListActivity extends AppCompatActivity {

    private RideListActivity activity;
    private RecyclerView rvRideList;
    private AppCompatTextView txtErrorText;

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
        rvRideList = findViewById(R.id.rvRideList);
        txtErrorText = findViewById(R.id.txtErrorText);
    }

    private void initViewAction() {

    }

    private void initViewListener() {

    }
}