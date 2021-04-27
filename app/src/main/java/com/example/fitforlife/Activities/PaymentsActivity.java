package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Adapters.PaymentMonthAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;


public class PaymentsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    boolean isCoachOrManager;
    private CoachInfo currentCoach = null;
    private CoachInfo currentManager = null;
    private UserInfo currentUser = null;

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private String[] months = new String[12];
    private PaymentMonthAdapter adapter;
    FirebaseFirestore fStore;
    Integer[] paymentsYears;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        fStore = FirebaseFirestore.getInstance();
        currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
        currentUser = FitForLifeDataManager.getInstance().getCurrentUser();
        currentManager = FitForLifeDataManager.getInstance().getCurrentManager();

        if (currentCoach != null || currentManager != null) {
            isCoachOrManager = true;
            currentUser = (UserInfo) getIntent().getSerializableExtra("currentUser");
            Log.d("userTrans", "get extra : " + currentUser.toString());
        } else if (currentUser != null)
            isCoachOrManager = false;


        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));


        // Bottom Navigation Bar - action Listener
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.payments);
        setNav();

        //user name & image
        TextView traineeName = findViewById(R.id.userNamePayment);
        ImageView userImage = findViewById(R.id.profilePicturePayment);
        traineeName.setText(currentUser.getFullName());
        getUserInfo(userImage, traineeName, currentUser.getId());

        // Spinner
        List<Integer> years = FitForLifeDataManager.getInstance().getAllPaymentsYears(currentUser);
        Date today = new Date();
        if (!years.contains((today.getYear() + 1900))) {//if current year is not added yet - add it to list
            years.add(today.getYear() + 1900);//add current years to spinner
        }
        if (!years.contains((today.getYear() + 1901))) {//if next year is not added yet - add it to list
            years.add(today.getYear() + 1901);//add next year to spinner
        }
        paymentsYears = years.toArray(new Integer[0]);
        Spinner spin = (Spinner) findViewById(R.id.yearSpinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the payments years list
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, paymentsYears);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(spinnerAdapter);
        spin.setSelection(years.indexOf((today.getYear() + 1900)));


        recyclerView = findViewById(R.id.payments_Months);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager gLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gLayoutManager);

        months = getResources().getStringArray(R.array.mothsOfTheYear);
        adapter = new PaymentMonthAdapter(this, months, paymentsYears[0], currentUser, isCoachOrManager);
        Log.d("userTrans", "added  user in payments activity : " + this.currentUser.toString());

        recyclerView.setAdapter(adapter);
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        adapter = new PaymentMonthAdapter(this, months, paymentsYears[position], currentUser, isCoachOrManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /**
     * OnClickMore METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(PaymentsActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(PaymentsActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }

    private void setNav() {
        if (isCoachOrManager)
            bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calendar:
                        if (currentManager != null) {
                            Intent calendarIntent = new Intent(PaymentsActivity.this, ManagerCalendarAcitvity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent calendarIntent = new Intent(PaymentsActivity.this, CalendarActivity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.progress:
                        if (isCoachOrManager) {
                            Intent Progress = new Intent(PaymentsActivity.this, TraineeProgressActivity.class);
                            startActivity(Progress);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent ProgressIntent = new Intent(PaymentsActivity.this, ProgressActivity.class);
                            startActivity(ProgressIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.home:
                        Intent homeIntent = new Intent(PaymentsActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.payments:
                        Intent paymentsIntent = new Intent(PaymentsActivity.this, PaymentsActivity.class);
                        startActivity(paymentsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        if (isCoachOrManager) {
                            Intent groupsIntent;
                            if (currentCoach != null) {
                                groupsIntent = new Intent(PaymentsActivity.this, GroupsTraineeActivity.class);
                            } else {
                                groupsIntent = new Intent(PaymentsActivity.this, ManagerCoachTraineeGroupsActivity.class);
                            }
                            startActivity(groupsIntent);
                        } else {
                            Intent profileIntent = new Intent(PaymentsActivity.this, ProfileActivity.class);
                            startActivity(profileIntent);
                        }
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        // END NAVIGATION BAR
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imageView);
                username.setText(user.getFullName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}