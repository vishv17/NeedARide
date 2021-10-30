package com.app.ride.authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.model.MessageModel;
import com.app.ride.authentication.model.UserModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    private void initView() {
        globals = new Globals();
        mAuth = FirebaseAuth.getInstance();

        if( globals.getFireBaseId() != null && (!globals.getFireBaseId().equals(""))){
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        public void run() {
                            checkDataAvailable();

                        }
                    }, 2000L);

        }else {
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this,SignUpActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000L);
        }


    }

    private void checkDataAvailable() {

        FirebaseFirestore.getInstance().collection(Constant.RIDE_USERS).
                document(globals.getFireBaseId()).
                collection(Constant.RIDE_USER_DATA).whereEqualTo(Constant.RIDE_Firebase_Uid,
                globals.getFireBaseId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if (Objects.requireNonNull(task.getResult()).getDocuments().size() > 0) {
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            UserModel userModel = snapshot.toObject(UserModel.class);
                           globals.setuserDetails(SplashActivity.this,userModel);
                            Intent intent = new Intent(SplashActivity.this,DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }else {
                        Intent intent = new Intent(SplashActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
}