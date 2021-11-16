package com.app.ride.authentication.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class PassengerActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatTextView tvDateOfJourney;
    RadioGroup radioGrpPets, radioGrpLuggage;
    String selectedStartPlace, selectedEndPlace, selectedDate = "";
    RadioButton selectPet, selectLuggage;
    AppCompatButton btnSubmit, btnDelete, btnChat;
    String[] country = {"India", "USA", "China", "Japan", "Other"};
    Globals globals;
    PassengerRequestModel model;
    Spinner spStartPlace, spEndPlace;
    private AppCompatImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);

        initView();
        setStatEndPlace();
    }

    private void initView() {
        globals = new Globals();
        tvDateOfJourney = findViewById(R.id.tvDateOfJourney);
        radioGrpPets = findViewById(R.id.radioGrpPets);
        radioGrpLuggage = findViewById(R.id.radioGrpLuggage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnDelete = findViewById(R.id.btnDelete);
        btnChat = findViewById(R.id.btnChat);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);

        tvDateOfJourney.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        btnChat.setOnClickListener(this);
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
        } else {
            btnSubmit.setVisibility(View.VISIBLE);
            btnChat.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            enableDisableViews(true);
        }
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
                    HashMap<String, String> data = new HashMap<>();
                    data.put(Constant.RIDE_Firebase_Uid, globals.getFireBaseId());
                    data.put(Constant.RIDE_DATE_OF_JOURNEY, selectedDate);
                    data.put(Constant.RIDE_START_PLACE, selectedStartPlace);
                    data.put(Constant.RIDE_END_PLACE, selectedEndPlace);
                    data.put(Constant.RIDE_name,globals.getUserDetails(PassengerActivity.this).getFirstName()+ " "+
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
            case R.id.btnChat:
                if(btnChat.getText().toString().equals(getResources().getString(R.string.request_list))){
                    redirectToChatListScreen();
                }else {
                    redirectToChatScreen();
                }
                break;

        }

    }
    private void redirectToChatListScreen() {
        Intent intent = new Intent(PassengerActivity.this, MessageListActivity.class);
        startActivity(intent);
    }

    private void redirectToChatScreen() {
        Intent intent = new Intent(PassengerActivity.this, MessageActivity.class);
        intent.putExtra(Constant.FD_OPPONENT_UID, model.getUid());
        if(model.getName()!=null && model.getName().equals("")){
            intent.putExtra(Constant.RIDE_name, "");
        }else {
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
}