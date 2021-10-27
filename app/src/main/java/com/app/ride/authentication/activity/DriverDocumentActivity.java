package com.app.ride.authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.skyhope.expandcollapsecardview.ExpandCollapseCard;
import com.skyhope.expandcollapsecardview.ExpandCollapseListener;

public class DriverDocumentActivity extends AppCompatActivity implements ExpandCollapseListener, View.OnClickListener {

    private ExpandCollapseCard cdDrivingLicense,cdNoc;
    private AppCompatImageView ivDriving,ivNoc;
    private AppCompatImageView ivBack;
    private Globals globals;
    private DriverDocumentActivity activity;
    private static final String TAG = "DriverDocuemntActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_document);
        activity = DriverDocumentActivity.this;

        initView();
        initViewListener();

    }

    private void initView() {
        globals = new Globals();
        cdDrivingLicense = findViewById(R.id.cdDrivingLicense);
        cdNoc = findViewById(R.id.cdNoc);
        ivDriving = cdDrivingLicense.getChildView().findViewById(R.id.image);
        ivNoc = cdNoc.getChildView().findViewById(R.id.image);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);

        loadDocuments();
    }

    private void loadDocuments() {
        FirebaseFirestore.getInstance().collection(Constant.RIDE_DRIVER_DOC_DATA)
                .document(globals.getFireBaseId()).
                collection(Constant.RIDE_DOC).whereEqualTo(Constant.RIDE_Firebase_Uid,globals.getFireBaseId()).
                get().addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        /*for(QueryDocumentSnapshot documentSnapshot : task.getResult())
                        {
                            Log.e(TAG, "loadDocuments:->"+documentSnapshot.getData());
                        }*/

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        if(documentSnapshot.get(Constant.RIDE_DRIVING)!=null && !documentSnapshot.get(Constant.RIDE_DRIVING).equals(""))
                        {
                            Glide.with(activity)
                                    .load(documentSnapshot.get(Constant.RIDE_DRIVING))
                                    .placeholder(R.drawable.ic_document)
                                    .into(ivDriving);
                        }

                        if(documentSnapshot.get(Constant.RIDE_NOC)!=null && !documentSnapshot.get(Constant.RIDE_NOC).equals(""))
                        {
                            Glide.with(activity)
                                    .load(documentSnapshot.get(Constant.RIDE_NOC))
                                    .placeholder(R.drawable.ic_document)
                                    .into(ivNoc);
                        }
                    }
                });
    }

    private void initViewListener() {
        cdDrivingLicense.initListener(this);
        cdNoc.initListener(b -> {

        });
        ivBack.setOnClickListener(this);
        ivDriving.setOnClickListener(view -> {
            Toast.makeText(activity, "Coming Soon!", Toast.LENGTH_SHORT).show();
        });
        ivNoc.setOnClickListener(view -> {
            Toast.makeText(activity, "Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onExpandCollapseListener(boolean b) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }
}