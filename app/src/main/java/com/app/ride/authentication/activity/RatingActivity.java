package com.app.ride.authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.model.RatingContainer;
import com.app.ride.authentication.model.RatingModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {

    private RatingActivity activity;
    private AppCompatTextView txtDriverName;
    private RatingBar rating;
    private AppCompatButton btnSubmit;
    private String driverId;
    private String requestId;
    private Globals globals;
    private List<RatingModel> ratingList;
    private static final String TAG = "RatingActivity";

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
        ratingList = new ArrayList<>();
        if(getIntent()!=null)
        {
            if(getIntent().hasExtra(Constant.RIDE_USER_ID))
            {
                driverId = getIntent().getStringExtra(Constant.RIDE_USER_ID);
            }

            if(getIntent().hasExtra(Constant.RIDE_REQUEST_ID))
            {
                requestId = getIntent().getStringExtra(Constant.RIDE_REQUEST_ID);
            }
        }
    }

    private void initViewAction() {
        if(requestId != null)
        {
            FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request)
                    .document(requestId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                String userId = documentSnapshot.get(Constant.RIDE_Firebase_Uid,String.class);
                                if(userId!=null)
                                {
                                    FirebaseFirestore.getInstance().collection(Constant.RIDE_USERS)
                                            .document(userId)
                                            .collection(Constant.RIDE_USER_DATA)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        DocumentSnapshot documentSnapshot1 = task.getResult().getDocuments().get(0);
                                                        String docId = documentSnapshot1.getId();
                                                        Log.e(TAG, "onComplete: documentSnapshot1-->"+documentSnapshot1.getId());
                                                        Log.e(TAG, "onComplete: documentSnapshot1-->"+documentSnapshot1.getData());
                                                        Map<String,Object> map = documentSnapshot1.getData();
                                                        String fName = (String) map.get("FirstName");
                                                        txtDriverName.setText(fName);
                                                        /*FirebaseFirestore.getInstance()
                                                                .collection(Constant.RIDE_USER_DATA)
                                                                .document(documentSnapshot1.getId())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if(task.isSuccessful())
                                                                        {
                                                                            DocumentSnapshot documentSnapshot2 = task.getResult();
                                                                            Log.e(TAG, "onComplete: documentSnapshot2"+documentSnapshot2.getData());

                                                                        }
                                                                    }
                                                                });*/
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                            else
                            {
                                Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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
                    updateRating(rating.getRating());
                }
                break;
        }
    }

    private void updateRating(float rating) {
        globals.showHideProgress(activity, true);
        FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request)
                .document(requestId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    ratingList =  Objects.requireNonNull(documentSnapshot.toObject(RatingContainer.class)).getRatingModelList();
                    HashMap<String, Object> data = new HashMap<>();
                    RatingModel ratingModel = new RatingModel();
                    ratingModel.setRequestId(requestId);
                    ratingModel.setUserId(globals.getFireBaseId());
                    ratingModel.setRating(rating);
                    if(ratingList==null)
                    {
                        ratingList = new ArrayList<>();
                    }
                    ratingList.add(ratingModel);
                    data.put(Constant.RATING_LIST,ratingList);
                    FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request)
                            .document(requestId)
                            .update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    globals.showHideProgress(activity, false);
                                    Toast.makeText(activity, "Rating updated Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    globals.showHideProgress(activity, false);
                                    Toast.makeText(activity, "Something Went Wrong,Please try Again Later", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                {
                    Log.e(TAG, "onComplete:-->"+task.getException().getMessage());
                    Toast.makeText(activity, "Something Went Wrong,Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}