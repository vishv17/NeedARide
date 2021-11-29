package com.app.ride.authentication.activity;

import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.ride.R;
import com.app.ride.authentication.model.PassengerRequestModel;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class PassengerActivity extends AppCompatActivity implements View.OnClickListener, PaymentResultListener {
    AppCompatTextView tvDateOfJourney;
    RadioGroup radioGrpPets, radioGrpLuggage;
    String selectedStartPlace, selectedEndPlace, selectedDate = "";
    RadioButton selectPet, selectLuggage;
    AppCompatButton btnSubmit, btnDelete, btnChat, btnPay;
    String[] country = {"India", "USA", "China", "Japan", "Other"};
    Globals globals;
    PassengerRequestModel model;
    Spinner spStartPlace, spEndPlace;
    private AppCompatImageView ivBack;
    int PAYPAL_REQUEST_CODE = 123;
    PayPalConfiguration config;
    private AppCompatButton btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);

        initView();
        setStatEndPlace();
    }

    private void initView() {

        Checkout.preload(getApplicationContext());

        globals = new Globals();
        tvDateOfJourney = findViewById(R.id.tvDateOfJourney);
        radioGrpPets = findViewById(R.id.radioGrpPets);
        radioGrpLuggage = findViewById(R.id.radioGrpLuggage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnDelete = findViewById(R.id.btnDelete);
        btnChat = findViewById(R.id.btnChat);
        btnPay = findViewById(R.id.btnPay);
        ivBack = findViewById(R.id.ivBack);
        btnConfirm = findViewById(R.id.btnConfirm);
        ivBack.setVisibility(View.VISIBLE);

        tvDateOfJourney.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnPay.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        /*config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).acceptCreditCards(true).
                clientId("AaTa8QSjo4-22iYzx60thgiqSvlCu0qPmX-H51M9QCpFIu9Rqak1J9S7IJtN2FxzExWoIyRGC0yzB2og");*/
        config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).
                clientId("AaTa8QSjo4-22iYzx60thgiqSvlCu0qPmX-H51M9QCpFIu9Rqak1J9S7IJtN2FxzExWoIyRGC0yzB2og");
    }

    private void setStatEndPlace() {
        spStartPlace = (Spinner) findViewById(R.id.spStartPlace);
        spStartPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedStartPlace = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<? extends String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStartPlace.setAdapter(aa);


        spEndPlace = (Spinner) findViewById(R.id.spEndPlace);
        spEndPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedEndPlace = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<? extends String> endAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, country);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEndPlace.setAdapter(endAdapter);


        Intent intent = getIntent();
        if (intent.hasExtra("DATA")) {
            model = (PassengerRequestModel) intent.getSerializableExtra("DATA");
            setDataIntoView(model);
            if (!model.getUid().equals(globals.getFireBaseId())) {
                btnConfirm.setVisibility(View.VISIBLE);
            } else {
                btnConfirm.setVisibility(View.GONE);
            }
            enableDisableViews(!(model.getRideStarted()));
        } else {
            btnSubmit.setVisibility(View.VISIBLE);
            btnChat.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            enableDisableViews(true);
        }
        btnConfirm.setVisibility(View.GONE);
    }

    private void enableDisableViews(boolean enable) {
        tvDateOfJourney.setEnabled(enable);
        spStartPlace.setEnabled(enable);
        spEndPlace.setEnabled(enable);
        enableDisableRadioGroup(radioGrpPets, enable);
        enableDisableRadioGroup(radioGrpLuggage, enable);
    }

    private void enableDisableRadioGroup(RadioGroup radioGroup, boolean enable) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(enable);
        }
    }

    private void setDataIntoView(PassengerRequestModel model) {
        tvDateOfJourney.setText(model.getDateOfJourney());
        selectedDate = model.getDateOfJourney();
        selectedStartPlace = model.getStartPlace();

        for (int i = 0; i < spStartPlace.getCount(); i++) {
            if (spStartPlace.getItemAtPosition(i).equals(selectedStartPlace)) {
                spStartPlace.setSelection(i);
                break;
            }
        }

        selectedEndPlace = model.getEndPlace();
        for (int i = 0; i < spEndPlace.getCount(); i++) {
            if (spEndPlace.getItemAtPosition(i).equals(selectedEndPlace)) {
                spEndPlace.setSelection(i);
                break;
            }
        }


        if (model.getLuggageAllow().equals(getResources().getString(R.string.text_yes))) {
            radioGrpLuggage.check(R.id.radioYesLuggage);
        } else {
            radioGrpLuggage.check(R.id.radioNoLuggage);

        }
        if (model.getPetsAllow().equals(getResources().getString(R.string.text_yes))) {
            radioGrpPets.check(R.id.radioYes);
        } else {
            radioGrpPets.check(R.id.radioNo);
        }

        btnSubmit.setText(getResources().getString(R.string.text_update));
        if (!globals.getFireBaseId().equals(model.getUid())) {
            btnSubmit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnChat.setVisibility(View.VISIBLE);
            enableDisableViews(false);
        } else {
            btnSubmit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnChat.setVisibility(View.VISIBLE);
            btnChat.setText(getResources().getString(R.string.request_list));
            enableDisableViews(true);
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
                    globals.showHideProgress(PassengerActivity.this, true);
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(Constant.RIDE_Firebase_Uid, globals.getFireBaseId());
                    data.put(Constant.RIDE_DATE_OF_JOURNEY, selectedDate);
                    data.put(Constant.RIDE_START_PLACE, selectedStartPlace);
                    data.put(Constant.RIDE_END_PLACE, selectedEndPlace);
                    data.put(Constant.RIDE_name, globals.getUserDetails(PassengerActivity.this).getFirstName() + " " +
                            globals.getUserDetails(PassengerActivity.this).getLastName());

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
                    data.put(Constant.RIDE_STARTED,false);

                    if (model != null && btnSubmit.getText().toString().equals(getResources().getString(R.string.text_update))) {
                        FirebaseFirestore.getInstance().collection(Constant.RIDE_passenger_request).whereEqualTo(Constant.RIDE_passenger_Uid, model.getPassengerId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        FirebaseFirestore.getInstance().collection(Constant.RIDE_passenger_request)
                                                .document(document.getId()).set(data, SetOptions.merge());
                                        showMessage("data updateed!!!!");
                                        globals.showHideProgress(PassengerActivity.this, false);
                                        finish();
                                    }
                                }
                            }
                        });
                    } else {
                        data.put(Constant.RIDE_passenger_Uid, String.valueOf(System.currentTimeMillis()));
                        FirebaseFirestore.getInstance().collection(Constant.RIDE_passenger_request).
                                add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    showMessage("data added!!!!");
                                    globals.showHideProgress(PassengerActivity.this, false);
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
                deletePassengerRequest();
                break;

            case R.id.btnPay:
                startPayment();
                break;

            case R.id.btnChat:
                if (btnChat.getText().toString().equals(getResources().getString(R.string.request_list))) {
                    redirectToChatListScreen();
                } else {
                    redirectToChatScreen();
                }
                // onInitPayPal();

                break;
            case R.id.btnConfirm:
                Intent intent = new Intent(PassengerActivity.this,RideListActivity.class);
                startActivity(intent);
                break;
        }
    }


    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "RIDE");
            options.put("description", "Ride Charges");
            options.put("send_sms_hash", true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "USD");
            options.put("amount", "100");

            JSONObject preFill = new JSONObject();
            preFill.put("email", "test@razorpay.com");
            preFill.put("contact", "9924204267");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }


    private void onInitPayPal() {
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        PayPalPayment payment = new PayPalPayment(new BigDecimal(1), "USD", "Test", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent paymentIntent = new Intent(this, PaymentActivity.class);
        paymentIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        paymentIntent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(paymentIntent, PAYPAL_REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {

            switch (resultCode) {
                case Activity.RESULT_OK: {
                    PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirm != null) {
                        Toast.makeText(this, "payment success...", Toast.LENGTH_LONG).show();

                    }

                }
                break;
                case Activity.RESULT_CANCELED: {
                    Toast.makeText(this, "payment fail...", Toast.LENGTH_LONG).show();
                }
                break;

            }
        }
    }

    private void redirectToChatListScreen() {
        Intent intent = new Intent(PassengerActivity.this, MessageListActivity.class);
        startActivity(intent);
    }

    private void redirectToChatScreen() {
        Intent intent = new Intent(PassengerActivity.this, MessageActivity.class);
        intent.putExtra(Constant.FD_OPPONENT_UID, model.getUid());
        if (model.getName() != null && model.getName().equals("")) {
            intent.putExtra(Constant.RIDE_name, "");
        } else {
            intent.putExtra(Constant.RIDE_name, model.getName());
        }
        intent.putExtra(Constant.RIDE_REQUEST_ID, model.getPassengerId());
        startActivity(intent);
    }


    private void deletePassengerRequest() {
        globals.showHideProgress(PassengerActivity.this, true);
        FirebaseFirestore.getInstance().collection(Constant.RIDE_passenger_request).whereEqualTo(Constant.RIDE_passenger_Uid, model.getPassengerId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FirebaseFirestore.getInstance().collection(Constant.RIDE_passenger_request)
                                .document(document.getId()).delete();
                        showMessage("data updateed!!!!");
                        globals.showHideProgress(PassengerActivity.this, false);
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

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(PassengerActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError", e);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}