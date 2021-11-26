package com.app.ride.authentication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;

import com.app.ride.R;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;

import java.util.HashMap;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {

    private RatingActivity activity;
    private AppCompatTextView txtDriverName;
    private RatingBar rating;
    private AppCompatButton btnSubmit;
    private String driverId;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        activity = RatingActivity.this;

        initView();
        initViewAction();
        initViewListener();
    }

    private void initView() {
        globals = new Globals();
        txtDriverName = findViewById(R.id.txtDriverName);
        rating = findViewById(R.id.rating);
        btnSubmit = findViewById(R.id.btnSubmit);
        if(getIntent()!=null)
        {
            if(getIntent().hasExtra(Constant.RIDE_USER_ID))
            {
                driverId = getIntent().getStringExtra(Constant.RIDE_USER_ID);
            }
        }
    }

    private void initViewAction() {

    }

    private void initViewListener() {
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnSubmit:
                if(driverId!=null && !driverId.isEmpty() && rating.getRating() > 0.0)
                {
                    updateRating();
                }
                break;
        }
    }

    private void updateRating() {
        globals.showHideProgress(activity, true);
        HashMap<String, Object> data = new HashMap<>();

    }
}