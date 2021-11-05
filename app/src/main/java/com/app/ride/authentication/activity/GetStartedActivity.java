package com.app.ride.authentication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.utility.Constant;

public class GetStartedActivity extends AppCompatActivity implements View.OnClickListener {

    private GetStartedActivity activity;

    private AppCompatEditText etPhoneNumber;
    private AppCompatButton btnLogin;
    private AppCompatTextView txtCreateAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        activity = this;

        initView();
        initViewListener();
    }

    private void initView() {
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnLogin = findViewById(R.id.btnLogin);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
    }

    private void initViewListener() {
        btnLogin.setOnClickListener(this);
        txtCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (valid()) {
                    Intent intent = new Intent(activity, VerifyPhoneActivity.class);
                    intent.putExtra(Constant.RIDE_MOBILE_NO,etPhoneNumber.getText().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.txtCreateAccount:
                Intent intent = new Intent(activity,SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
    }

    private boolean valid() {
        String mobile = etPhoneNumber.getText().toString();
        if (mobile.isEmpty()) {
            Toast.makeText(activity, "Please Enter the Mobile No.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (mobile.length() != 10) {
            Toast.makeText(activity, "Please Enter a Valid Mobile No.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}