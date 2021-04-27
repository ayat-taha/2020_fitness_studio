package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.fitforlife.Adapters.PaymentsAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TraineePaymentsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ListView traineeListView;
    private BottomNavigationView bottomNavigationView;
    FirebaseFirestore fStore;
    private CoachInfo currentCoach;
    private CoachInfo currentManager;
    List<Group> groupsTable;
    boolean isCoach;
    boolean isManager;
    EditText inputSearch;
    PaymentsAdapter adapter;


    ArrayList<String> groupsNames;
    String[] groupNamesArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_payments);
        fStore = FirebaseFirestore.getInstance();
        currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
        currentManager = FitForLifeDataManager.getInstance().getCurrentManager();
        if (currentCoach == null && currentManager != null) {
            isManager = true;
            isCoach = false;
        } else {
            isCoach = true;
            isManager = false;
        }

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        // Bottom Navigation Bar - action Listener
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.payments);
        bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);
        setNav();

        traineeListView = findViewById(R.id.list_TraineePayments);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                TraineePaymentsActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //spinner
        Spinner spin = (Spinner) findViewById(R.id.groupSpinner);
        if (isCoach) {
            groupsTable = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
        } else if (isManager) {
            groupsTable = FitForLifeDataManager.getInstance().getAllManagerGroups(currentManager);
        }
        groupsNames = new ArrayList<>();
        groupsNames.add(this.getResources().getString(R.string.allGroups));

        for (Group tmpG : groupsTable)
            groupsNames.add(tmpG.getGroupName()); //add groups names to spinner list

        spin.setSelection(0); //set selection to all groups
        groupNamesArr = groupsNames.toArray(new String[groupsNames.size()]);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, groupNamesArr);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(spinnerAdapter); //Setting the ArrayAdapter data on the Spinner
        spin.setOnItemSelectedListener(this);
    }

    /**
     * logout method
     *
     * @param view - logout icon
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(TraineePaymentsActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }

    /**
     * More METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(TraineePaymentsActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    private void setNav() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calendar:
                        if (isManager) {
                            Intent calendarIntent = new Intent(TraineePaymentsActivity.this, ManagerCalendarAcitvity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent calendarIntent = new Intent(TraineePaymentsActivity.this, CalendarActivity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.progress:
                        if (isCoach || isManager) {
                            Intent postIntent = new Intent(TraineePaymentsActivity.this, TraineeProgressActivity.class);
                            startActivity(postIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent ProgressIntent = new Intent(TraineePaymentsActivity.this, ProgressActivity.class);
                            startActivity(ProgressIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.home:
                        Intent homeIntent = new Intent(TraineePaymentsActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.payments:
                        Intent paymentsIntent;
                        if (isCoach || isManager) {
                            paymentsIntent = new Intent(TraineePaymentsActivity.this, TraineePaymentsActivity.class);
                        } else {
                            paymentsIntent = new Intent(TraineePaymentsActivity.this, PaymentsActivity.class);
                        }
                        startActivity(paymentsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        if (isManager) {
                            Intent groupsIntent = new Intent(TraineePaymentsActivity.this, ManagerCoachTraineeGroupsActivity.class);
                            startActivity(groupsIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                        if (isCoach) {
                            Intent groupsIntent = new Intent(TraineePaymentsActivity.this, GroupsTraineeActivity.class);
                            startActivity(groupsIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent profileIntent = new Intent(TraineePaymentsActivity.this, ProfileActivity.class);
                            startActivity(profileIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                }
                return false;
            }
        });
        // END NAVIGATION BAR
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        List<UserInfo> currentTrainee = new ArrayList<>();
        String groupName = groupNamesArr[position];
        if (position == 0) {
            if (isCoach)
                currentTrainee = FitForLifeDataManager.getInstance().getAllCoachTrainee(currentCoach);
            else if (isManager)
                currentTrainee = FitForLifeDataManager.getInstance().getAllManagerTrainee(currentManager);
            if (!currentTrainee.isEmpty()) {
                Context context = getApplicationContext();
                adapter = new PaymentsAdapter(context, R.layout.card_trainee_payments, currentTrainee);
                traineeListView.setAdapter(adapter);
            }
        } else {
            if (isCoach) {
                currentTrainee = FitForLifeDataManager.getInstance().getAllGroupTrainee(currentCoach, groupName);
                adapter = new PaymentsAdapter(this, R.layout.card_trainee_payments, currentTrainee);
                traineeListView.setAdapter(adapter);
            }
            if (isManager) {
                currentTrainee = FitForLifeDataManager.getInstance().getAllTrainee(groupName);
                adapter = new PaymentsAdapter(this, R.layout.card_trainee_payments, currentTrainee);
                traineeListView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


}
