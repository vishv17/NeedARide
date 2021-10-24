package com.app.ride.authentication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.model.UserModel;
import com.app.ride.authentication.utility.Globals;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ProfileDetailsActivity activity;
    private CircleImageView ivProfileDetails;
    private AppCompatTextView txtMobileNo,txtFirstName,txtLastName;
    private CardView cdEdit,cdUploadDocument,cdSignOut;
    private AppCompatImageView ivBack;
    private Globals globals;
    private static final String TAG = "ProfileDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        activity = ProfileDetailsActivity.this;

        initView();
    }

    private void initView() {
        globals = new Globals();
        ivProfileDetails = findViewById(R.id.ivProfileDetails);
        txtMobileNo = findViewById(R.id.txtMobileNo);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        cdEdit = findViewById(R.id.cdEdit);
        cdUploadDocument = findViewById(R.id.cdUploadDocument);
        cdSignOut = findViewById(R.id.cdSignOut);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);

        if(globals.getUserDetails(activity) != null)
        {
            UserModel userModel = globals.getUserDetails(activity);
            Glide.with(activity)
                    .load(globals.getUserDetails(activity).getProfilePic())
                    .into(ivProfileDetails);
            txtFirstName.setText(userModel.getFirstName());
            txtLastName.setText(userModel.getLastName());
        }

        cdEdit.setOnClickListener(this);
        cdUploadDocument.setOnClickListener(this);
        cdSignOut.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.cdEdit:
            case R.id.cdUploadDocument:
            case R.id.cdSignOut:
                Toast.makeText(activity,"Coming Soon!",Toast.LENGTH_LONG).show();
                break;
        }
    }
}