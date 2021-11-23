package com.app.ride.authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.model.UserModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatEditText etCode;
    private AppCompatButton btnSubmit;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
        Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        globals =  new Globals();

        initView();
    }

    private void initView() {
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        etCode = findViewById(R.id.etCoder);
        etCode.requestFocus();

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        Intent intent = getIntent();
        String mobile = intent.getStringExtra(Constant.RIDE_MOBILE_NO);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        sendVerificationCode(mobile);
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
//                        .setPhoneNumber("+1" + "6505554567")       // Phone number to verify
                        .setPhoneNumber("+91" + mobile)       // Phone number to verify
//                        .setPhoneNumber("+1" + mobile)       // Phone number to verify
                        .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        globals.showHideProgress(VerifyPhoneActivity.this,true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        globals.showHideProgress(VerifyPhoneActivity.this,false);

                        if (task.isSuccessful()) {
                            FirebaseFirestore.getInstance().collection(Constant.RIDE_USERS).
                                    document(globals.getFireBaseId()).
                                    collection(Constant.RIDE_USER_DATA).whereEqualTo(Constant.RIDE_Firebase_Uid,
                                    task.getResult().getUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (Objects.requireNonNull(task.getResult()).getDocuments().size() > 0) {
                                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                                UserModel userModel = snapshot.toObject(UserModel.class);
                                                globals.setuserDetails(VerifyPhoneActivity.this, userModel);
                                                Intent intent = new Intent(VerifyPhoneActivity.this, DashboardActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }


                                        } else {
                                            /*Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
                                            startActivity(intent);
                                            finish();*/
                                            Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });
                        } else {
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSubmit) {
            String code = etCode.getText().toString().trim();
            if (code.length() != 6) {
                Toast.makeText(VerifyPhoneActivity.this,getString(R.string.err_code),Toast.LENGTH_LONG).show();
                return;
            }
            verifyVerificationCode(code);
        }
    }
}