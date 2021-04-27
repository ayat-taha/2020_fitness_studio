package com.example.fitforlife.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.fitforlife.Adapters.TraineeAdapter;
import com.example.fitforlife.Adapters.UserReportAdapter;
import com.example.fitforlife.Fragments.Trainee_Fragment;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class paymentsReportsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    List<UserInfo> users;
    List<Group> groupsTable;
    private CoachInfo currentCoach;
    private CoachInfo currentManager;
    boolean isCoach;
    boolean isManager;
    String groupName;
    int year;
    int month;
    ArrayList<String> groupsNames;
    String[] groupNamesArr;
    private String[] months = new String[12];
    List<Integer> years;
    UserReportAdapter adapter;
    ListView traineeListView;
    EditText inputSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_reports);

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

        users = FitForLifeDataManager.getInstance().getAllCoachTrainee(currentCoach);
        traineeListView = findViewById(R.id.listForPaymentsReport);

        inputSearch = (EditText) findViewById(R.id.inputReportSearch);
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
               adapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //group spinner
        Spinner GroupSpin = (Spinner) findViewById(R.id.reportGroup);
        if (isCoach) {
            groupsTable = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
        } else if (isManager) {
            groupsTable = FitForLifeDataManager.getInstance().getAllManagerGroups(currentManager);
        }
        groupsNames = new ArrayList<>();
        groupsNames.add(this.getResources().getString(R.string.allGroups));

        for (Group tmpG : groupsTable)
            groupsNames.add(tmpG.getGroupName()); //add groups names to spinner list

        GroupSpin.setSelection(0); //set selection to all groups
        groupNamesArr = groupsNames.toArray(new String[groupsNames.size()]);
        ArrayAdapter spinnerGroupAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, groupNamesArr);
        spinnerGroupAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        GroupSpin.setAdapter(spinnerGroupAdapter); //Setting the ArrayAdapter data on the Spinner
        GroupSpin.setOnItemSelectedListener(this);

        //month spinner
        Spinner monthSpin = (Spinner) findViewById(R.id.reportMonth);
        months = getResources().getStringArray(R.array.mothsOfTheYear);
        ArrayAdapter spinnerMonthAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, months);
        spinnerMonthAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        monthSpin.setAdapter(spinnerMonthAdapter); //Setting the ArrayAdapter data on the Spinner
        monthSpin.setOnItemSelectedListener(this);

        //year spinner
        Spinner yearSpin = (Spinner) findViewById(R.id.reportYear);
        years = FitForLifeDataManager.getInstance().getAllPaymentsYears();
        ArrayAdapter spinnerYearAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, years);
        spinnerYearAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        yearSpin.setAdapter(spinnerYearAdapter); //Setting the ArrayAdapter data on the Spinner
        yearSpin.setOnItemSelectedListener(this);
    }

    public void OnClickSearch(View view) {

        if (groupName.equals(this.getResources().getString(R.string.allGroups))) {

            users = FitForLifeDataManager.getInstance().getAllNotPaidReport(month, year);
            List<UserInfo> tmpUsers = users;

            for (UserInfo u : tmpUsers) {
                if (!groupsTable.contains(new Group(u.getGroupId()))) {
                    users.remove(u);
                }
            }
        } else {
            Group group = FitForLifeDataManager.getInstance().getGroupByName(groupName);
            Log.d("reportActivity", "name" + group);
            Log.d("reportActivity", "month " + month);
            Log.d("reportActivity", "year  " + year);

            users = FitForLifeDataManager.getInstance().getGroupPaidReport(group, month, year);

            Log.d("reportActivity", "users " + users);

        }
        adapter = new UserReportAdapter(this, R.layout.card_trainee_report, users, true);
        traineeListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long arg3) {
        int id = parent.getId();
        TraineeAdapter adapter;
        switch (id) {
            case R.id.reportGroup:
                groupName = groupNamesArr[position];
                break;
            case R.id.reportMonth:
                month = position + 1;
                break;
            case R.id.reportYear:
                year = years.get(position);
                break;
        }
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
        Intent MoreIntent = new Intent(paymentsReportsActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(paymentsReportsActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();
    }
}