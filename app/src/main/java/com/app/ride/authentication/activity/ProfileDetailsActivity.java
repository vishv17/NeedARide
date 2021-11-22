package com.app.ride.authentication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.model.UserModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ProfileDetailsActivity activity;
    private CircleImageView ivProfileDetails;
    private AppCompatTextView txtMobileNo,txtFirstName,txtLastName;
    private CardView cdEdit,cdUploadDocument,cdSignOut;
    private AppCompatImageView ivBack;
    private Globals globals;
    private static final String TAG = "ProfileDetailsActivity";
    UserModel userModel;

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
            userModel = globals.getUserDetails(activity);
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
                Intent intent1 =new Intent(ProfileDetailsActivity.this, ProfileActivity.class);
                intent1.putExtra(Constant.RIDE_EDIT,"edit");
                intent1.putExtra(Constant.RIDE_firstName,userModel.getFirstName());
                intent1.putExtra(Constant.RIDE_lastName,userModel.getLastName());
                intent1.putExtra(Constant.RIDE_profileImage,globals.getUserDetails(activity).getProfilePic());
                startActivity(intent1);
                break;
            case R.id.cdUploadDocument:
                Intent intent = new Intent(activity,DriverDocumentActivity.class);
                startActivity(intent);
                break;
            case R.id.cdSignOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(ProfileDetailsActivity.this,SignUpActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                finish();
                break;
        }
    }
}