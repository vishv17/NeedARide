package com.app.ride.authentication.activity;

import android.app.DatePickerDialog;
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
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.ride.R;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class DriverRideActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatTextView tvDateOfJourney;
    AppCompatEditText etVehicleNumber, etNumberOfSeatAvailable, etCostPerSeat;
    RadioGroup radioGrpPets, radioGrpLuggage;
    String selectedStartPlace, selectedEndPlace,selectedDate = "";
    RadioButton selectPet, selectLuggage;
    AppCompatButton btnSubmit;
    String[] country = {"India", "USA", "China", "Japan", "Other"};
Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride);

        initView();
        setStatEndPlace();
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

        tvDateOfJourney.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void setStatEndPlace() {
        Spinner spin = (Spinner) findViewById(R.id.spStartPlace);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spin.setAdapter(aa);


        Spinner endSpin = (Spinner) findViewById(R.id.spEndPlace);
        endSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        endSpin.setAdapter(endAdapter);
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
                    HashMap<String, String> data = new HashMap<>();
                    data.put(Constant.RIDE_Firebase_Uid, globals.getFireBaseId());
                    data.put(Constant.RIDE_DATE_OF_JOURNEY, selectedDate);
                    data.put(Constant.RIDE_START_PLACE, selectedStartPlace);
                    data.put(Constant.RIDE_END_PLACE, selectedEndPlace);
                    data.put(Constant.RIDE_vehicle_number, etVehicleNumber.getText().toString());
                    data.put(Constant.RIDE_seat_available, etNumberOfSeatAvailable.getText().toString());
                    data.put(Constant.RIDE_cost_per_seat, etCostPerSeat.getText().toString());

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

                    FirebaseFirestore.getInstance().collection(Constant.RIDE_Driver_request).add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                showMessage("data added!!!!");
                                globals.showHideProgress(DriverRideActivity.this,false);
                                finish();
                            }
                        }
                    });
                }
                break;
            }
        }
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