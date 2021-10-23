package com.app.ride.authentication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.utility.Constant;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatEditText etPhoneNo;
    private AppCompatButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initView();
    }

    private void initView() {

        etPhoneNo = findViewById(R.id.etPhoneNumber);
        etPhoneNo.requestFocus();
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSubmit) {
            String mobile = etPhoneNo.getText().toString().trim();

            if (mobile.isEmpty() || mobile.length() < 10) {
                Toast.makeText(SignUpActivity.this,getString(R.string.err_phone_no),Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(SignUpActivity.this, VerifyPhoneActivity.class);
            intent.putExtra(Constant.RIDE_MOBILE_NO, mobile);
            startActivity(intent);
        }
    }
}