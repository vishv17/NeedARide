package com.app.ride.authentication.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.skyhope.expandcollapsecardview.ExpandCollapseCard;
import com.skyhope.expandcollapsecardview.ExpandCollapseListener;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class DriverDocumentActivity extends AppCompatActivity implements ExpandCollapseListener, View.OnClickListener {

    private ExpandCollapseCard cdDrivingLicense, cdNoc;
    private AppCompatImageView ivDriving, ivNoc;
    private AppCompatImageView ivBack;
    private Globals globals;
    private DriverDocumentActivity activity;
    private static final String TAG = "DriverDocuemntActivity";
    private ActivityResultLauncher activityResultLauncher;
    private String typeImage, drivingImage, nocImage;
    private String licenseCategory = "";
    private TextRecognizer textRecognizer;
    private InputImage licenseInputImage;
    private String drivingLicenseUrl, nocUrl;
    private String drivingImageDownload;

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
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
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
                collection(Constant.RIDE_DOC).whereEqualTo(Constant.RIDE_Firebase_Uid, globals.getFireBaseId()).
                get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                if (documentSnapshot.get(Constant.RIDE_DRIVING) != null && !documentSnapshot.get(Constant.RIDE_DRIVING).equals("")) {
                    drivingLicenseUrl = documentSnapshot.get(Constant.RIDE_DRIVING, String.class);
                    Glide.with(activity)
                            .load(documentSnapshot.get(Constant.RIDE_DRIVING))
                            .placeholder(R.drawable.ic_document)
                            .into(ivDriving);
                }

                if (documentSnapshot.get(Constant.RIDE_NOC) != null && !documentSnapshot.get(Constant.RIDE_NOC).equals("")) {
                    nocUrl = documentSnapshot.get(Constant.RIDE_NOC, String.class);
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
//            Toast.makeText(activity, "Coming Soon!", Toast.LENGTH_SHORT).show();
            typeImage = getResources().getString(R.string.text_driving_license);
            showDialog(typeImage);
        });
        ivNoc.setOnClickListener(view -> {
            typeImage = getResources().getString(R.string.text_noc);
            showDialog(typeImage);
//            Toast.makeText(activity, "Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }

    public void showDialog(String imageType) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_dialoge);

        TextView tvGallery = (TextView) dialog.findViewById(R.id.tvGallery);
        TextView tvCamera = (TextView) dialog.findViewById(R.id.tvCamera);

        TextView dialogButton = (TextView) dialog.findViewById(R.id.tvCancel);
        dialogButton.setOnClickListener(v -> dialog.dismiss());
        tvGallery.setOnClickListener(v -> {
            EasyImage.openGallery(activity, 0);
            dialog.dismiss();
        });
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openCameraForImage(activity, 0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean checkDataForLicense() {
        boolean isValidData = true;
        Text extractedText = null;
        if (drivingImage != null) {
            Uri licenseImageUri = Uri.fromFile(new File(drivingImage));
            Log.e(TAG, "licenseImageUri: " + licenseImageUri.toString());
            try {
                licenseInputImage = InputImage.fromFilePath(getApplicationContext(), licenseImageUri);
                Task<Text> result = textRecognizer.process(licenseInputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(@NonNull Text text) {
                                Log.e(TAG, "onSuccess: Success->" + text.getText());
//                                extractedText[0] = text;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e.getMessage());
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Text>() {
                            @Override
                            public void onComplete(@NonNull Task<Text> task) {

                            }
                        });
                if (result.isSuccessful()) {
                    extractedText = result.getResult();
                    isValidData = validateData(extractedText);
                } else {
                    isValidData = false;
                }
            } catch (IOException e) {
                Log.e(TAG, "checkData: Convert Image to text Error->" + e.getMessage());
                e.printStackTrace();
            }

        }
        Log.e(TAG, "checkDataForLicense: isValidate->" + String.valueOf(isValidData));
        return isValidData;
    }

    private boolean validateData(Text text) {
        String result = text.getText();
        String expDate = "";
        boolean returnResult = true;
        int blockCount = 0;
        for (Text.TextBlock block : text.getTextBlocks()) {
            String blockText = block.getText();
            Log.e(TAG, "onSuccess: blockText--->" + blockText);
            blockCount += 1;
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            Log.e(TAG, "onSuccess: BlockCount--->" + blockCount);
            if (blockCount == 13) {
                licenseCategory = blockText;
            }
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Log.e(TAG, "onSuccess: LineText--->" + lineText);
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                if (lineText.contains("EXPI EXP")) {
                    int elementsSize = line.getElements().size();
                    expDate = line.getElements().get((elementsSize - 1)).getText().toString();
                }
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Log.e(TAG, "onSuccess: ElementText--->" + elementText);
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
        Log.e(TAG, "License Category:->" + licenseCategory.toLowerCase().trim().toString());
        //If License Category is G1 or Not fetched properly then return as false
        if (licenseCategory.toLowerCase().trim().toString().equals("g1") || (licenseCategory.toLowerCase().trim().toString().isEmpty())) {
            returnResult = false;
        } else {
            //If License is expired then return as false
            if (!compareDate(expDate)) {
                returnResult = false;
            }
        }
        return returnResult;
    }

    private boolean compareDate(String expDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/mm/dd");
        try {
            Date date = format.parse(expDate);
            if (new Date().after(date)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                if (typeImage.equals(getResources().getString(R.string.text_driving_license))) {
                    drivingImage = imageFiles.get(0).getAbsolutePath();
                    Glide.with(activity)
                            .load(drivingImage)
                            .into(ivDriving);
                    if (!checkDataForLicense()) {
                        Toast.makeText(activity, getResources().getString(R.string.license_doc_upload_error), Toast.LENGTH_LONG).show();
                        replaceDriverLicense();
                    } else {
                    }
                } else {
                    nocImage = imageFiles.get(0).getAbsolutePath();
                    Glide.with(activity)
                            .load(nocImage)
                            .into(ivNoc);
//                    checkDataForNoc();
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(activity);
                    photoFile.delete();
                }
            }
        });
    }

    private void replaceDriverLicense() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference sRef = firebaseStorage.getReferenceFromUrl(drivingLicenseUrl);
        sRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                uploadNewImage();
            }
        });
    }

    private void uploadNewImage() {
        globals.showHideProgress(activity, true);

        String randomName = String.valueOf(System.currentTimeMillis());

        StorageReference filePath = FirebaseStorage.getInstance().getReference().
                child(Constant.RIDE_Firebase_DOCUMENT).child(randomName + ".jpg");

        filePath.putFile(Uri.fromFile(new File(drivingImage))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(@NonNull Uri uri) {
                            drivingImageDownload = uri.toString();
                            HashMap<String, String> data = new HashMap<>();
                            data.put(Constant.RIDE_Firebase_Uid, globals.getFireBaseId());
                            data.put(Constant.RIDE_DRIVING, drivingImageDownload);
                            FirebaseFirestore.getInstance().collection(Constant.RIDE_DRIVER_DOC_DATA).
                                    document(globals.getFireBaseId()).collection(Constant.RIDE_DOC).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(activity, "Document Updated Successfully", Toast.LENGTH_LONG).show();
                                    globals.showHideProgress(activity, false);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onExpandCollapseListener(boolean b) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }
}