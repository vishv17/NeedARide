package com.app.ride.authentication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView ivProfile;
    private AppCompatEditText etName;
    private AppCompatEditText etLastName;
    private AppCompatButton tvComplete;
    private String photoUploadUrl;
Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
    }

    private void initView() {
        globals = new Globals();
        ivProfile = findViewById(R.id.ivProfile);
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        tvComplete = findViewById(R.id.tvComplete);
        ivProfile.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivProfile) {
            TedPermission.with(this).setPermissionListener(new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    showDialog();
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {

                }
            }).setDeniedMessage(getString(R.string.on_denied_permission))
                    .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();

        } else if (v.getId() == R.id.tvComplete) {
            if (Valid()) {
                addDataOnDatabase();
            }
        }
    }

    private void addDataOnDatabase() {
        globals.showHideProgress(ProfileActivity.this,true);
        FirebaseFirestore.getInstance().collection(Constant.RIDE_USERS).
                document(globals.getFireBaseId()).
                collection(Constant.RIDE_USER_DATA).whereEqualTo(Constant.RIDE_Firebase_Uid,
                globals.getFireBaseId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() > 0) {
                        Toast.makeText(ProfileActivity.this, "data already added", Toast.LENGTH_LONG).show();
                        globals.showHideProgress(ProfileActivity.this,false);

                    } else {
                        String randomName = FieldValue.serverTimestamp().toString();

                        //upload image
                        StorageReference filepath = FirebaseStorage.getInstance().getReference()
                                .child(Constant.RIDE_PROFILE_IMAGE)
                                .child(randomName + ".jpg");

                        filepath.putFile(Uri.fromFile(new File(String.valueOf(Uri.parse(photoUploadUrl))))).
                                addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    HashMap<String, String> data = new HashMap<>();
                                                    data.put(Constant.RIDE_Firebase_Uid, globals.getFireBaseId());
                                                    data.put(Constant.RIDE_Firebase_FirstName, etName.getText().toString().trim());
                                                    data.put(Constant.RIDE_Firebase_LastName, etLastName.getText().toString().trim());
                                                    data.put(Constant.RIDE_Firebase_ProfilePic, uri.toString());
                                                    FirebaseFirestore.getInstance().collection(Constant.RIDE_USERS).
                                                            document(globals.getFireBaseId()).collection("Data").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(ProfileActivity.this, "data added", Toast.LENGTH_LONG).show();
                                                            globals.showHideProgress(ProfileActivity.this,false);
                                                            Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            });
                                        }

                                    }
                                });
                    }

                }

            }
        });

    }

    private boolean Valid() {
        if(photoUploadUrl!=null) {
            if (photoUploadUrl.trim().length() <= 0) {
                return false;
            }
        }
        else
        {
            return false;
        }
        if (etName.getText().toString().trim().length() <= 0) {
            return false;
        }
        if (etLastName.getText().toString().trim().length() <= 0) {
            return false;
        }
        return true;
    }


    public void showDialog() {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_dialoge);

        TextView tvGallery = (TextView) dialog.findViewById(R.id.tvGallery);
        TextView tvCamera = (TextView) dialog.findViewById(R.id.tvCamera);


        TextView dialogButton = (TextView) dialog.findViewById(R.id.tvCancel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openGallery(ProfileActivity.this, 0);
                dialog.dismiss();
            }
        });
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openCameraForImage(ProfileActivity.this, 0);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, ProfileActivity.this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                photoUploadUrl = imageFiles.get(0).getAbsolutePath().toString();
                Glide.with(ProfileActivity.this)
                        .load(photoUploadUrl)
                        .into(ivProfile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ProfileActivity.this);
                    photoFile.delete();
                }
            }
        });

    }
}