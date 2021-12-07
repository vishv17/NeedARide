package com.app.ride.authentication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ride.R;
import com.app.ride.authentication.model.DocumentApprovalModel;
import com.app.ride.authentication.model.UserModel;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;
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
import com.skyhope.expandcollapsecardview.ExpandCollapseCard;
import com.skyhope.expandcollapsecardview.ExpandCollapseListener;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    private String typeImage, drivingImage, nocImage;
    private String licenseCategory = "";
    private TextRecognizer textRecognizer;
    private InputImage licenseInputImage, nocInputImage;
    private String drivingLicenseUrl, nocUrl;
    private String drivingImageDownload, nocImageDownload;
    private final int PICK_PDF_CODE = 101;
    private String PATH_FILE = "";
    private AppCompatTextView txtFileName;

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
        txtFileName = cdNoc.getChildView().findViewById(R.id.txtFileName);
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
                if (task.getResult().getDocuments().size() > 0) {
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
                        if(globals.getUserDetails(activity)!=null)
                        {
                            UserModel userModel = globals.getUserDetails(activity);
                            if(userModel.getFirstName()!=null)
                            {
                                txtFileName.setText(userModel.getFirstName()+"_NOC");
                            }
                        }
                    }
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
            TedPermission.with(activity).setPermissionListener(new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    showDialog(typeImage);
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {

                }
            }).setDeniedMessage(getString(R.string.on_denied_permission))
                    .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();
        });
        ivNoc.setOnClickListener(view -> {
            typeImage = getResources().getString(R.string.text_noc);
            showPDFDialog();
//            showDialog(typeImage);
//            Toast.makeText(activity, "Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showPDFDialog() {
        Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
        intentPDF.setType("application/pdf");
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intentPDF, "Select PDF"), PICK_PDF_CODE);
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

    private class NocDataTask extends AsyncTask<Void, Void, Void> {
        boolean isValidData = false;
        Text extractedText = null;
        Uri nocImageUri;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nocImageUri = Uri.fromFile(new File(nocImage));
            try {
                nocInputImage = InputImage.fromFilePath(getApplicationContext(), nocImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (textRecognizer == null) {
                textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            textRecognizer.process(nocInputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(@NonNull Text text) {
                            Log.e(TAG, "onSuccess: Success->" + text.getText());
                            String[] splittedString = text.getText().split("\n");
                            List<String> splittedArrayList = Arrays.asList(splittedString);
                            for (String s : splittedArrayList) {
                                Log.e(TAG, "onSuccess: Splitted String -->" + s);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            }).addOnCompleteListener(new OnCompleteListener<Text>() {
                @Override
                public void onComplete(@NonNull Task<Text> task) {
                    if (task.isSuccessful()) {
                        extractedText = task.getResult();
                        isValidData = validateNocData(extractedText);
                        if (isValidData) {
                            Glide.with(activity)
                                    .load(drivingImage)
                                    .into(ivDriving);
                            replaceDriverLicense();
                        } else {
                            /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                            // set title
                            alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage("Something Went Wrong with the image uploaded into the app, Please check the image and re-upload")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // if this button is clicked, close
                                            // current activity
                                            dialog.dismiss();
//                                finish();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();*/
                        }
                    } else {
                        Log.e(TAG, "doInBackground: Result is false");
                    }
                }
            });
            Log.e(TAG, "doInBackground: Called");
            return null;
        }
    }

    private class LicenseDataTask extends AsyncTask<Void, Void, Void> {
        boolean isValidData = false;
        Text extractedtext = null;
        Uri licenseImageUri;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            licenseImageUri = Uri.fromFile(new File(drivingImage));
            try {
                licenseInputImage = InputImage.fromFilePath(getApplicationContext(), licenseImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (textRecognizer == null) {
                textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            textRecognizer.process(licenseInputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(@NonNull Text text) {
                            Log.e(TAG, "onSuccess: Success->" + text.getText());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            }).addOnCompleteListener(new OnCompleteListener<Text>() {
                @Override
                public void onComplete(@NonNull Task<Text> task) {
                    if (task.isSuccessful()) {
                        extractedtext = task.getResult();
                        isValidData = validateLicenseData(extractedtext);
                        if (isValidData) {
                            Glide.with(activity)
                                    .load(drivingImage)
                                    .into(ivDriving);
                            replaceDriverLicense();
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                            // set title
                            alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage("Something Went Wrong with the image uploaded into the app, Please check the image and re-upload")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // if this button is clicked, close
                                            // current activity
                                            dialog.dismiss();
//                                finish();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();
                        }
                    } else {
                        Log.e(TAG, "doInBackground: Result is false");
                    }
                }
            });
            Log.e(TAG, "doInBackground: Called");
            return null;
        }

        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            Log.e(TAG, "onPostExecute: Called");
            /*if (extractedtext != null) {
                isValidData = validateLicenseData(extractedtext);
            }*/
        }
    }

    private boolean checkDataForLicense() {
        boolean isValidData = false;
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
                    isValidData = validateLicenseData(extractedText);
                }
            } catch (Exception e) {
                Log.e(TAG, "checkData: Convert Image to text Error->" + e.getMessage());
                e.printStackTrace();
            }

        }
        Log.e(TAG, "checkDataForLicense: isValidate->" + String.valueOf(isValidData));
        return isValidData;
    }

    private boolean validateLicenseData(Text text) {
        Log.e(TAG, "validateLicenseData: Inside this function");
        String result = text.getText();
        String expDate = "";
        boolean returnResult = false;
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
            else
            {
                returnResult = true;
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
        if (requestCode == PICK_PDF_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    Log.e(TAG, "onActivityResult: URI-->" + uri.toString());
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    if (myFile != null) {
                        txtFileName.setText(getFileName(uriString));
                    }
                    PATH_FILE = myFile.getAbsolutePath();
                    replaceNocDocument(uri);
                } else {
                    Log.e(TAG, "onActivityResult: URI-->URI is null");
                }
                Glide.with(activity)
                        .load(R.drawable.ic_document)
                        .into(ivNoc);
            }
        } else {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                    if (typeImage.equals(getResources().getString(R.string.text_driving_license))) {
                        drivingImage = imageFiles.get(0).getAbsolutePath();
                    /*if (!checkDataForLicense()) {
                        Toast.makeText(activity, getResources().getString(R.string.license_doc_upload_error), Toast.LENGTH_LONG).show();
                    } else {
                        Glide.with(activity)
                                .load(drivingImage)
                                .into(ivDriving);
                        replaceDriverLicense();
                    }*/
                        new LicenseDataTask().execute();
                    } else {
                        nocImage = imageFiles.get(0).getAbsolutePath();
                        new NocDataTask().execute();
                    /*if (checkDataForNoc()) {
                        Glide.with(activity)
                                .load(nocImage)
                                .into(ivNoc);
                    } else {

                    }*/

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
    }

    private void replaceNocDocument(Uri uri) {
        globals.showHideProgress(activity, true);
        File myFile = new File(uri.toString());
        String randomName = String.valueOf(System.currentTimeMillis());

        StorageReference filePath = FirebaseStorage.getInstance().getReference().
                child(Constant.RIDE_Firebase_DOCUMENT).child(randomName + ".pdf");
        filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(@NonNull Uri uri) {
                            nocImageDownload = uri.toString();
                            HashMap<String, Object> data = new HashMap<>();
//                            data.put(Constant.RIDE_Firebase_Uid, globals.getFireBaseId());
                            data.put(Constant.RIDE_NOC, nocImageDownload);

                            FirebaseFirestore.getInstance().collection(Constant.RIDE_DRIVER_DOC_DATA)
                                    .document(globals.getFireBaseId()).
                                    collection(Constant.RIDE_DOC).whereEqualTo(Constant.RIDE_Firebase_Uid, globals.getFireBaseId()).
                                    get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().getDocuments()!=null && task.getResult().getDocuments().size() > 0) {
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            Log.e(TAG, "onComplete: documentSnapShot id-->" + documentSnapshot.getId());
                                            DocumentApprovalModel nocModel = new DocumentApprovalModel();
                                            nocModel.setModel(Constant.NOC);
                                            nocModel.setDocUrl(nocImageDownload);
                                            nocModel.setApproval(false);
                                            nocModel.setUserId(globals.getFireBaseId());
                                            FirebaseFirestore.getInstance().collection(Constant.RIDE_DRIVER_NOC_APPROVAL)
                                                    .document(globals.getFireBaseId())
                                                    .set(nocModel)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                FirebaseFirestore.getInstance().collection(Constant.RIDE_DRIVER_DOC_DATA).
                                                                        document(globals.getFireBaseId()).collection(Constant.RIDE_DOC).
                                                                        document(documentSnapshot.getId()).
                                                                        update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(@NonNull Void unused) {
                                                                        Toast.makeText(activity, "Document Updated Successfully", Toast.LENGTH_LONG).show();
                                                                        globals.showHideProgress(activity, false);
                                                                    }
                                                                });
                                                            }
                                                            else
                                                            {
                                                                globals.showHideProgress(activity,false);
                                                                Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                        else
                                        {
                                            Toast.makeText(activity, "Document Updated Successfully", Toast.LENGTH_LONG).show();
                                            globals.showHideProgress(activity, false);
                                        }
                                    }
                                }
                            });
                        }
                    });
                } else {
                    globals.showHideProgress(activity, false);
                }
            }
        });
    }

    private boolean checkDataForNoc() {
        boolean isValidData = false;
        Text extractedText = null;
        if (textRecognizer == null) {
            textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        }
        if (nocImage != null) {
            Uri nocImageUri = Uri.fromFile(new File(nocImage));
            Log.e(TAG, "NocImage: " + nocImageUri.toString());
            try {
                nocInputImage = InputImage.fromFilePath(getApplicationContext(), nocImageUri);
                Task<Text> result = textRecognizer.process(nocInputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(@NonNull Text text) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Text>() {
                            @Override
                            public void onComplete(@NonNull Task<Text> task) {

                            }
                        });
                if (result.isSuccessful()) {
                    extractedText = result.getResult();
                    isValidData = validateNocData(extractedText);
                } else {
                    Log.e(TAG, "checkDataForNoc: Unable to read NOC Data for the uploaded document");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "checkDataForNoc: Convert Image to Text Failed Error->" + e.getMessage());
            }
        }
        return isValidData;
    }

    private boolean validateNocData(Text extractedText) {
        String result = extractedText.toString();
        String expDate = "";
        boolean returnResult = false;
        int blockCount = 0;
        String[] splittedString = result.toLowerCase().split("\n");
        Log.e(TAG, "validateNocData: splittedString Array Size-->" + splittedString.length);
        List<String> textList = Arrays.asList(splittedString);
        Log.e(TAG, "validateNocData: splittedString List Size-->" + textList.size());
        for (String s : textList) {
//            Log.e(TAG, "validateNocData: text-->"+s);
        }
        if (textList.contains("g1")) {
            Log.e(TAG, "validateNocData: Returned");
            return false;
        }
//        ArrayList<String> splittedStringList = ArrayList(new (result.split("\n")));

        for (Text.TextBlock block : extractedText.getTextBlocks()) {
            String blockText = block.getText();
            Log.e(TAG, "validateNocData: BlockText-->" + blockText);
            blockCount += 1;
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            Log.e(TAG, "validateNocData: BlockCount-->" + blockCount);
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Log.e(TAG, "validateNocData: LineText-->" + lineText);
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Log.e(TAG, "validateNocData: Element text-->" + elementText);
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }

        }
        return returnResult;
    }

    private void replaceDriverLicense() {
        if (drivingLicenseUrl != null) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference sRef = firebaseStorage.getReferenceFromUrl(drivingLicenseUrl);
            sRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(@NonNull Void unused) {
                    uploadNewImage();
                }
            });
        }
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
                                    DocumentApprovalModel drivingModel = new DocumentApprovalModel();
                                    drivingModel.setModel(Constant.DRIVING);
                                    drivingModel.setDocUrl(drivingImageDownload);
                                    drivingModel.setApproval(false);
                                    drivingModel.setUserId(globals.getFireBaseId());
                                    FirebaseFirestore.getInstance().collection(Constant.RIDE_DRIVER_LICENSE_APPROVAL)
                                            .document(globals.getFireBaseId())
                                            .set(drivingModel)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    globals.showHideProgress(activity, false);
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(activity, "Document Updated Successfully", Toast.LENGTH_LONG).show();
                                                    }
                                                    else {
                                                        Toast.makeText(activity, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    });
                } else {
                    globals.showHideProgress(activity, false);
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

    private String getFileName(String imageUri) {
        String result = "";
        if (imageUri.startsWith("content")) {
            Cursor cursor = getContentResolver().query(Uri.parse(imageUri), null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = Uri.parse(imageUri).getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }
}