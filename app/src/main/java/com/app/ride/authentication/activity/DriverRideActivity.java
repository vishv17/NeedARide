package com.app.ride.authentication.activity;

import static com.app.ride.authentication.utility.Constant.ACCEPTED_ID;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.ride.R;
import com.app.ride.authentication.model.DriverRequestModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.app.ride.authentication.utility.MySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DriverRideActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatTextView tvDateOfJourney;
    AppCompatEditText etVehicleNumber, etNumberOfSeatAvailable, etCostPerSeat;
    private AppCompatImageView ivBack;
    RadioGroup radioGrpPets, radioGrpLuggage;
    String selectedStartPlace, selectedEndPlace, selectedDate = "";
    RadioButton selectPet, selectLuggage;
    AppCompatButton btnSubmit, btnDelete, btnChat, btnRideStart, btnConfirm, btnRideEnd;
    AppCompatEditText spStartPlace, spEndPlace;
    Globals globals;
    DriverRequestModel model;
    private static final String TAG = "DriverRideActivity";
    private String refreshToken;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String contentType = "application/json";
    private final String serverKey = "key=AAAAvgzBmsk:APA91bG2rz5lOW0WL78U_p-944xIXwnulUU-gEIxwGyTlB-_bX35e2SKTeDNU1jRlh5qmkrfAHHvc00WW66jFb50M6rJ5qsm0CuBxYs6XokBK1nFcGR93gBpFpOfWTnEz8mB1GOXBpSf";
    private final String SENDER_ID = "816257800905";
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    private String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride);

        initView();
        initViewListener();
        setStatEndPlace();
        updateToken();
    }

    private void updateToken() {
        Log.e(TAG, "updateToken: " + globals.getFireBaseId());
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                String token = task.getResult();
                globals.setFCMToken(DriverRideActivity.this, token);
                Log.e(TAG, "onComplete: Toke is-->" + token);
            }
        });
//        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    private void initViewListener() {
        btnRideStart.setOnClickListener(this);
        btnRideEnd.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }


    private void initView() {
        globals = new Globals();

        tvDateOfJourney = findViewById(R.id.tvDateOfJourney);
        etVehicleNumber = findViewById(R.id.etVehicleNumber);
        etNumberOfSeatAvailable = findViewById(R.id.etNumberOfSeatAvailable);
        etCostPerSeat = findViewById(R.id.etCostPerSeat);
        radioGrpPets = findViewById(R.id.radioGrpPets);
        radioGrpLuggage = findViewById(R.id.radioGrpLuggage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnDelete = findViewById(R.id.btnDelete);
        btnChat = findViewById(R.id.btnChat);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnRideStart = findViewById(R.id.btnRideStart);
        btnRideEnd = findViewById(R.id.btnRideEnd);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);

        tvDateOfJourney.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        ivBack.setOnClickListener(this);



    }

    private void setDataIntoView(DriverRequestModel model) {
        Log.e(TAG, "setDataIntoView: UID->" + model.getUid());
        tvDateOfJourney.setText(model.getDateOfJourney());
        selectedDate = model.getDateOfJourney();
        etVehicleNumber.setText(model.getVehicleNumber());
        etNumberOfSeatAvailable.setText(String.valueOf(model.getSeatAvailable()));
        etCostPerSeat.setText(model.getCostPerSeat());
        selectedStartPlace = model.getStartPlace();

        spStartPlace.setText(selectedStartPlace);
        selectedEndPlace = model.getEndPlace();

        spEndPlace.setText(selectedEndPlace);

        radioGrpLuggage.check(model.getLuggageAllow().equals(getResources().getString(R.string.text_yes)) ? R.id.radioYesLuggage : R.id.radioNoLuggage);
        radioGrpPets.check(model.getPetsAllow().equals(getResources().getString(R.string.text_yes)) ? R.id.radioYes : R.id.radioNo);
        btnSubmit.setText(getResources().getString(R.string.text_update));
        if (!globals.getFireBaseId().equals(model.getUid())) {
            btnSubmit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnChat.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);
            enableDisableViews(false);
        } else {
            btnSubmit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnChat.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);
            btnChat.setText(getResources().getString(R.string.request_list));
            enableDisableViews(true);
        }
        if (model.getAcceptedId() != null) {
            if (model.getAcceptedId().contains(globals.getFCMToken(DriverRideActivity.this))) {
                btnConfirm.setVisibility(View.GONE);
            } else {
                btnConfirm.setVisibility(View.VISIBLE);
            }
        }
        hideVisibleRideStartEnd(model);
    }

    private void hideVisibleRideStartEnd(DriverRequestModel model) {
        if(model.isRideCompleted())
        {
            btnRideStart.setVisibility(View.GONE);
            btnRideEnd.setVisibility(View.GONE);
        }
        else
        {
            if(model.isRideStarted() && model.getUid().equals(globals.getFireBaseId()))
            {
                btnRideStart.setVisibility(View.GONE);
                btnRideEnd.setVisibility(View.VISIBLE);
            }
            else if(model.getUid().equals(globals.getFireBaseId()))
            {
                btnRideStart.setVisibility(View.VISIBLE);
                btnRideEnd.setVisibility(View.GONE);
            }
            else
            {
                btnRideStart.setVisibility(View.GONE);
                btnRideEnd.setVisibility(View.GONE);
            }
        }
    }

    private void setStatEndPlace() {
        spStartPlace = (AppCompatEditText) findViewById(R.id.spStartPlace);



        spEndPlace = (AppCompatEditText) findViewById(R.id.spEndPlace);

        Intent intent = getIntent();
        if (intent.hasExtra("DATA")) {
            model = (DriverRequestModel) intent.getSerializableExtra("DATA");
            setDataIntoView(model);
            enableDisableViews(!(model.isRideStarted()));
        } else {
            btnDelete.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            btnRideStart.setVisibility(View.GONE);
            enableDisableViews(true);
        }
        if(intent.hasExtra(Constant.RIDE_REQUEST_ID))
        {
            requestId = intent.getStringExtra(Constant.RIDE_REQUEST_ID);
            Log.e(TAG, "setStatEndPlace: requestId-->"+requestId);
        }
    }


    private void enableDisableViews(boolean enable) {
        tvDateOfJourney.setEnabled(enable);
        spStartPlace.setEnabled(enable);
        spEndPlace.setEnabled(enable);
        etVehicleNumber.setEnabled(enable);
        etNumberOfSeatAvailable.setEnabled(enable);
        etCostPerSeat.setEnabled(enable);
        enableDisableRadioGroup(radioGrpPets, enable);

        enableDisableRadioGroup(radioGrpPets, enable);
        enableDisableRadioGroup(radioGrpLuggage, enable);
    }

    private void enableDisableRadioGroup(RadioGroup radioGroup, boolean enable) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(enable);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvDateOfJourney: {
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat dateFormatter = new SimpleDateFormat(myFormat, Locale.US);
                final Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        selectedDate = dateFormatter.format(newDate.getTime());
                        tvDateOfJourney.setText(selectedDate);
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                StartTime.getDatePicker().setMinDate(System.currentTimeMillis());
                StartTime.show();
                break;
            }

            case R.id.btnSubmit: {
                if (valid()) {
                    globals.showHideProgress(DriverRideActivity.this, true);
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(Constant.RIDE_Firebase_Uid, globals.getFireBaseId());
                    data.put(Constant.RIDE_DATE_OF_JOURNEY, selectedDate);
                    data.put(Constant.RIDE_START_PLACE, spStartPlace.getText().toString());
                    data.put(Constant.RIDE_END_PLACE, spEndPlace.getText().toString());
                    data.put(Constant.RIDE_vehicle_number, etVehicleNumber.getText().toString());
                    data.put(Constant.RIDE_seat_available, etNumberOfSeatAvailable.getText().toString());
                    data.put(Constant.RIDE_cost_per_seat, etCostPerSeat.getText().toString());
                    data.put(Constant.RIDE_name, globals.getUserDetails(DriverRideActivity.this).getFirstName() + " " +
                            globals.getUserDetails(DriverRideActivity.this).getLastName());

                    // get selected radio button from radioGroup
                    int selectedId = radioGrpPets.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    selectPet = (RadioButton) findViewById(selectedId);
                    data.put(Constant.RIDE_pets_allow, selectPet.getText().toString());

                    // get selected radio button from radioGroup
                    int selectedIdLuggage = radioGrpLuggage.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    selectLuggage = (RadioButton) findViewById(selectedIdLuggage);
                    data.put(Constant.RIDE_luggage_allow, selectLuggage.getText().toString());
                    ArrayList<String> fcmList = new ArrayList<>();
//                    fcmList.add(globals.getFCMToken(DriverRideActivity.this));
                    data.put(ACCEPTED_ID, fcmList);
                    data.put(Constant.RIDE_STARTED,false);
                    data.put(Constant.RIDE_COMPLETED,false);
                    if (model != null && btnSubmit.getText().toString().equals(getResources().getString(R.string.text_update))) {
                        FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request).
                                whereEqualTo(Constant.RIDE_driver_Uid, model.getDriverId()).get().
                                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request)
                                                        .document(document.getId()).set(data, SetOptions.merge());
                                                showMessage("data updateed!!!!");
                                                globals.showHideProgress(DriverRideActivity.this, false);
                                                finish();
                                            }
                                        }

                                    }
                                });
                    } else {
                        data.put(Constant.RIDE_driver_Uid, String.valueOf(System.currentTimeMillis()));

                        FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request).add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    showMessage("data added!!!!");
                                    globals.showHideProgress(DriverRideActivity.this, false);
                                    finish();
                                }
                            }
                        });
                    }

                }
                break;
            }
            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.btnDelete:
                deleteDriverRequest();
                break;

            case R.id.btnChat:
                if (btnChat.getText().toString().equals(getResources().getString(R.string.request_list))) {
                    redirectToChatListScreen();
                } else {
                    redirectToChatScreen();
                }
                break;
            case R.id.btnRideStart:
                updateRideStatus(true);
                break;
            case R.id.btnRideEnd:
                updateRideStatus(false);
//                sendNotificationforEndRide();
                break;
            case R.id.btnConfirm:
                if (model.getSeatAvailable() > 0) {
                    addBooking();
                } else {
                    Toast.makeText(DriverRideActivity.this, "Sorry No Seat is available", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void updateRideStatus(boolean status) {
        globals.showHideProgress(DriverRideActivity.this, true);
        HashMap<String, Object> data = new HashMap<>();
        if (status) {
            data.put(Constant.RIDE_STARTED, true);
            data.put(Constant.RIDE_COMPLETED, false);
        } else {
            data.put(Constant.RIDE_STARTED, false);
            data.put(Constant.RIDE_COMPLETED, true);
        }
        FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request).
                whereEqualTo(Constant.RIDE_driver_Uid, model.getDriverId()).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request)
                                        .document(document.getId()).set(data, SetOptions.merge());
                                globals.showHideProgress(DriverRideActivity.this, false);
                                if (status) {
                                    sendNotificationForRideStart();
                                } else {
                                    sendNotificationforEndRide();
                                }
                            }

                            HashMap<String,Object> hashMap = new HashMap<>();
//                            hashMap.put(Constant.RIDE_ID,requestId);
                            if(status)
                            {
                                hashMap.put(Constant.RIDE_STARTED,true);
                            }
                            else {
                                hashMap.put(Constant.RIDE_STARTED,false);
                            }

                            FirebaseFirestore.getInstance()
                                    .collection(Constant.RIDE_passenger_request)
                                    .whereEqualTo(Constant.RIDE_ID,requestId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                for(QueryDocumentSnapshot documentSnapshot : task.getResult())
                                                {
                                                    FirebaseFirestore.getInstance()
                                                            .collection(Constant.RIDE_passenger_request)
                                                            .document(documentSnapshot.getId())
                                                            .set(hashMap);
                                                }
                                            }
                                        }
                                    });
                        } else {
                            globals.showHideProgress(DriverRideActivity.this, false);
                            Toast.makeText(DriverRideActivity.this, "Error Occured while Updating Data", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void sendNotificationforEndRide() {
        NOTIFICATION_TITLE = getString(R.string.app_name);
        NOTIFICATION_MESSAGE = "Ride End";

        for (String s : model.getAcceptedId()) {
            JSONObject notification = new JSONObject();
            JSONObject notifcationBody = new JSONObject();
            try {
                notifcationBody.put("title", NOTIFICATION_TITLE);
                notifcationBody.put("message", NOTIFICATION_MESSAGE);
                notifcationBody.put("end", String.valueOf(true));
                notifcationBody.put(Constant.RIDE_USER_ID, globals.getFireBaseId());
                notifcationBody.put(Constant.RIDE_REQUEST_ID,requestId);
                notification.put("to",
                        s);
                notification.put("data", notifcationBody);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "sendNotification: " + e.getMessage());
            }

            sendNotificationApiCall(notification);
        }
        Toast.makeText(DriverRideActivity.this, "Ride Ended", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void addBooking() {
        globals.showHideProgress(DriverRideActivity.this, true);
        HashMap<String, Object> data = new HashMap<>();
        ArrayList<String> fcmList = new ArrayList<>();
        fcmList.add(globals.getFCMToken(DriverRideActivity.this));
        data.put(Constant.RIDE_seat_available, (model.getSeatAvailable() - 1));
        data.put("acceptedId", fcmList);

        FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request).
                whereEqualTo(Constant.RIDE_driver_Uid, model.getDriverId()).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request)
                                        .document(document.getId()).set(data, SetOptions.merge());
                                showMessage("data updateed!!!!");
                                globals.showHideProgress(DriverRideActivity.this, false);
                                finish();
                            }
                        } else {
                            globals.showHideProgress(DriverRideActivity.this, false);
                            Toast.makeText(DriverRideActivity.this, "Error Occured while Updating Data", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });

    }

    private void redirectToChatListScreen() {
        Intent intent = new Intent(DriverRideActivity.this, MessageListActivity.class);
        startActivity(intent);
    }

    private void sendNotificationForRideStart() {
        NOTIFICATION_TITLE = "Notification Title";
        NOTIFICATION_MESSAGE = "Ride Start";

        for (String s : model.getAcceptedId()) {
            JSONObject notification = new JSONObject();
            JSONObject notifcationBody = new JSONObject();

            try {
                notifcationBody.put("title", NOTIFICATION_TITLE);
                notifcationBody.put("message", NOTIFICATION_MESSAGE);
                notifcationBody.put("end", String.valueOf(false));
                notifcationBody.put(Constant.RIDE_USER_ID, globals.getFireBaseId());
                notifcationBody.put(Constant.RIDE_REQUEST_ID,requestId);
            /*notification.put("to",
                    "rvcFda6QMvOH4Gsw8MS83Qq6d9e2");*/
                notification.put("to",
                        s);
                notification.put("data", notifcationBody);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "sendNotification: " + e.getMessage());
            }

            sendNotificationApiCall(notification);
        }
        Toast.makeText(DriverRideActivity.this, "Ride is started.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void sendNotificationApiCall(JSONObject notification) {
        Log.e(TAG, "sendNotificationApiCall: notification Data-->" + notification.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse:-->" + response.toString());
                btnRideStart.setEnabled(false);
                /*btnRideEnd.setVisibility(View.VISIBLE);
                btnRideStart.setVisibility(View.GONE);*/
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: error-->" + error.getMessage());
                        Toast.makeText(DriverRideActivity.this, "Request error", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    private void redirectToChatScreen() {
        Intent intent = new Intent(DriverRideActivity.this, MessageActivity.class);
        intent.putExtra(Constant.FD_OPPONENT_UID, model.getUid());
        intent.putExtra(Constant.RIDE_REQUEST_ID, model.getDriverId());
        if (model.getName() != null && model.getName().equals("")) {
            intent.putExtra(Constant.RIDE_name, "");
        } else {
            intent.putExtra(Constant.RIDE_name, model.getName());
        }
        startActivity(intent);
    }

    private void deleteDriverRequest() {
        globals.showHideProgress(DriverRideActivity.this, true);

        FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request).
                whereEqualTo(Constant.RIDE_driver_Uid, model.getDriverId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request)
                                        .document(document.getId()).delete();
                                showMessage("data delete!!!!");
                                globals.showHideProgress(DriverRideActivity.this, false);
                                finish();
                            }
                        }

                    }
                });
    }


    private boolean valid() {
        if (selectedDate.trim().equals("")) {
            showMessage(getResources().getString(R.string.err_date));
            return false;
        }

        if (Objects.requireNonNull(etVehicleNumber.getText()).toString().equals("")) {
            showMessage(getResources().getString(R.string.err_vehicle));
            return false;
        }
        if (etNumberOfSeatAvailable.getText().toString().equals("")) {
            showMessage(getResources().getString(R.string.err_number_seat));
            return false;
        }

        if (etCostPerSeat.getText().toString().equals("")) {
            showMessage(getResources().getString(R.string.err_cost_per_seat));
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(DriverRideActivity.this, message, Toast.LENGTH_LONG).show();
    }
}