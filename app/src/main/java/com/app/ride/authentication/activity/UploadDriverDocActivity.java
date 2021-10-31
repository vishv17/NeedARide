package com.app.ride.authentication.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.ride.R;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class UploadDriverDocActivity extends AppCompatActivity implements View.OnClickListener {

    ConstraintLayout clDrivingLicense, clNoc;
    AppCompatTextView tvDrivingLicense, tvNoc;
    AppCompatImageView ivDrivingLicense, ivNoc, ivBack;
    AppCompatButton btnSubmit;
    String typeImage, drivingImage, nocImage;
    String drivingImageDownload, nocImageDownload;
    Globals globals;
    private static final String TAG = "UploadDriverDocActivity";

    private TextRecognizer textRecognizer;
    private InputImage licenseInputImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_driver_doc);


        clDrivingLicense = findViewById(R.id.clDrivingLicense);
        clNoc = findViewById(R.id.clNoc);
        tvDrivingLicense = findViewById(R.id.tvDrivingLicense);
        tvNoc = findViewById(R.id.tvNoc);
        ivDrivingLicense = findViewById(R.id.ivDrivingLicense);
        ivNoc = findViewById(R.id.ivNoc);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        initView();
    }

    private void initView() {
        globals = new Globals();
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        initViewListener();
    }

    private void initViewListener() {
        clDrivingLicense.setOnClickListener(this);
        clNoc.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.clDrivingLicense) {
            TedPermission.with(this).setPermissionListener(new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    typeImage = getResources().getString(R.string.text_driving_license);
                    showDialog();
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {

                }
            }).setDeniedMessage(getString(R.string.on_denied_permission))
                    .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();

        } else if (view.getId() == R.id.clNoc) {
            TedPermission.with(this).setPermissionListener(new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    typeImage = getResources().getString(R.string.text_noc);
                    showDialog();
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {

                }
            }).setDeniedMessage(getString(R.string.on_denied_permission))
                    .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();

        } else if (view.getId() == R.id.btnSubmit) {
            if (valid()) {
                uploadImagesToDatabase();
            }

        } else if (view.getId() == R.id.ivBack) {
            onBackPressed();
        }
    }

    private void uploadImagesToDatabase() {
        globals.showHideProgress(UploadDriverDocActivity.this,true);

        String randomName = String.valueOf(System.currentTimeMillis());


        //upload image
        StorageReference filePath = FirebaseStorage.getInstance().getReference().
                child(Constant.RIDE_Firebase_DOCUMENT).child(randomName + ".jpg");

        filePath.putFile(Uri.fromFile(new File(drivingImage))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            drivingImageDownload = uri.toString();
                            String randomName = String.valueOf(System.currentTimeMillis());
                            //upload image
                            StorageReference filePath =
                                    FirebaseStorage.getInstance().getReference().child(Constant.RIDE_Firebase_DOCUMENT).
                                            child(randomName + ".jpg");

                            filePath.putFile(Uri.fromFile(new File(nocImage))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                nocImageDownload = uri.toString();
                                                HashMap<String, String> data = new HashMap<>();
                                                data.put(Constant.RIDE_Firebase_Uid, globals.getFireBaseId());
                                                data.put(Constant.RIDE_DRIVING, drivingImageDownload);
                                                data.put(Constant.RIDE_NOC, nocImageDownload);
                                                FirebaseFirestore.getInstance().collection(Constant.RIDE_DRIVER_DOC_DATA).
                                                        document(globals.getFireBaseId()).collection(Constant.RIDE_DOC).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Toast.makeText(UploadDriverDocActivity.this, "data added", Toast.LENGTH_LONG).show();
                                                        globals.showHideProgress(UploadDriverDocActivity.this,false);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    });
                }
            }
        });
    }


    private boolean valid() {
        if (drivingImage.equals("") || drivingImage.length() < 0) {
            Toast.makeText(UploadDriverDocActivity.this, getResources().getString(R.string.err_driving_license), Toast.LENGTH_LONG).show();
            return false;
        }
        if (nocImage.equals("") || nocImage.length() < 0) {
            Toast.makeText(UploadDriverDocActivity.this, getResources().getString(R.string.err_noc), Toast.LENGTH_LONG).show();
            return false;
        }
        if(!checkData())
        {

        }
        return true;
    }

    private boolean checkData() {
        if(drivingImage!=null)
        {
            Uri licenseImageUri = Uri.fromFile(new File(drivingImage));
            Log.e(TAG, "licenseImageUri: "+licenseImageUri.toString());
            try {
                licenseInputImage = InputImage.fromFilePath(getApplicationContext(),licenseImageUri);
                Task<Text> result = textRecognizer.process(licenseInputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(@NonNull Text text) {
                                Log.e(TAG, "onSuccess: Success->"+text.getText());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: "+e.getMessage());
                            }
                        });
            } catch (IOException e) {
                Log.e(TAG, "checkData: Convert Image to text Error->"+e.getMessage());
                e.printStackTrace();
            }

        }
        return false;
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(UploadDriverDocActivity.this);
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
                EasyImage.openGallery(UploadDriverDocActivity.this, 0);
                dialog.dismiss();
            }
        });
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openCameraForImage(UploadDriverDocActivity.this, 0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, UploadDriverDocActivity.this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                if (typeImage.equals(getResources().getString(R.string.text_driving_license))) {
                    tvDrivingLicense.setVisibility(View.GONE);
                    ivDrivingLicense.setVisibility(View.VISIBLE);
                    drivingImage = imageFiles.get(0).getAbsolutePath();
                    Glide.with(UploadDriverDocActivity.this)
                            .load(drivingImage)
                            .into(ivDrivingLicense);
                    checkData();
                } else {
                    tvNoc.setVisibility(View.GONE);
                    ivNoc.setVisibility(View.VISIBLE);
                    nocImage = imageFiles.get(0).getAbsolutePath();
                    Glide.with(UploadDriverDocActivity.this)
                            .load(nocImage)
                            .into(ivNoc);
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(UploadDriverDocActivity.this);
                    photoFile.delete();
                }
            }
        });
    }
}