package com.app.ride.authentication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.ride.R;
import com.app.ride.authentication.adapter.MyAdapter;
import com.app.ride.authentication.utility.Constant;
import com.app.ride.authentication.utility.Globals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    Globals globals;
    FloatingActionButton floatingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initView();
    }

    private void initView() {
        globals = new Globals();
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        floatingBtn = (FloatingActionButton) findViewById(R.id.floatingBtn);

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_driver)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_passenger)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final MyAdapter adapter = new MyAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    checkForDocuments();
                } else {
                    Intent intent = new Intent(DashboardActivity.this, PassengerActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void checkForDocuments() {
        globals.showHideProgress(DashboardActivity.this,true);
        FirebaseFirestore.getInstance().collection(Constant.RIDE_DRIVER_DOC_DATA)
                .document(globals.getFireBaseId()).
                collection(Constant.RIDE_DOC).whereEqualTo(Constant.RIDE_Firebase_Uid,globals.getFireBaseId()).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (Objects.requireNonNull(task.getResult()).getDocuments().size() > 0) {
                        Intent intent = new Intent(DashboardActivity.this, DriverRideActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(DashboardActivity.this, UploadDriverDocActivity.class);
                        startActivity(intent);
                    }
                    globals.showHideProgress(DashboardActivity.this,false);
                }
            }
        });
    }
}