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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fitforlife.Adapters.TraineeAdapter;
import com.example.fitforlife.Adapters.UserReportAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class JoinedReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "JoinedReportActivity";

    private boolean isManager;
    private CoachInfo currentCoachManger;
    ListView traineeListView;
    List<UserInfo> coachTrainee;
    List<Group> groupsTable;
    EditText inputSearch;
    UserReportAdapter adapter;
    int year, month;
    private String[] months = new String[13];
    List<Integer> years;
    List<UserInfo> users;
    private CoachInfo currentCoach;
    private CoachInfo currentManager;
    String groupName;

    ArrayList<String> groupsNames;
    String[] groupNamesArr;
    ImageView pieChartBtn;
    TextView numOfAge, numOfAllJoiend;
    LinearLayout ageReportLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_report);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));


        if (FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentManager() != null) {
            isManager = true;
            currentCoachManger = FitForLifeDataManager.getInstance().getCurrentManager();
        } else {
            isManager = false;
            currentCoachManger = FitForLifeDataManager.getInstance().getCurrentCoach();

        }
//        traineeListView = findViewById(R.id.listForJoinedReport);
//        if (!isManager) {
//            coachTrainee = FitForLifeDataManager.getInstance().getAllCoachTrainee(currentCoachManger);
//
//
//        } else if (isManager) {
//            coachTrainee = FitForLifeDataManager.getInstance().getAllManagerTrainee(currentCoachManger);
//
//        }

        traineeListView = findViewById(R.id.listForJoinedReport);

        pieChartBtn = findViewById(R.id.pieChartBtn);
        pieChartBtn.setOnClickListener(pieOnClick);
        numOfAllJoiend = findViewById(R.id.numOfAllJoined);
        ageReportLayout = findViewById(R.id.ageReportLayout);
        ageReportLayout.setVisibility(View.GONE);

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
        if (!isManager) {
            groupsTable = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoachManger.getEmail());
        } else if (isManager) {
            groupsTable = FitForLifeDataManager.getInstance().getAllManagerGroups(currentCoachManger);
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
        months[0] = "All Months";
        for (int i = 1; i < months.length; i++)
            months[i] = getResources().getStringArray(R.array.mothsOfTheYear)[i - 1];
        ArrayAdapter spinnerMonthAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, months);
        spinnerMonthAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        monthSpin.setAdapter(spinnerMonthAdapter); //Setting the ArrayAdapter data on the Spinner
        monthSpin.setOnItemSelectedListener(this);

        //year spinner
        Spinner yearSpin = (Spinner) findViewById(R.id.reportYear);
        years = FitForLifeDataManager.getInstance().getAllJoinedYears();
        ArrayAdapter spinnerYearAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, years);
        spinnerYearAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        yearSpin.setAdapter(spinnerYearAdapter); //Setting the ArrayAdapter data on the Spinner
        yearSpin.setOnItemSelectedListener(this);
    } // end on create


    public void OnClickSearch(View view) {

        if (groupName.equals(this.getResources().getString(R.string.allGroups))) {
            if (isManager) {
                if (month == 0)
                    users = FitForLifeDataManager.getInstance().getAllJoinedThisYearManager(currentCoachManger, year);
                else
                    users = FitForLifeDataManager.getInstance().getAllJoinedManager(currentCoachManger, month, year);

            } else {
                if (month == 0)
                    users = FitForLifeDataManager.getInstance().getAllJoinedThisYearCoach(currentCoachManger, year);
                else
                    users = FitForLifeDataManager.getInstance().getAllJoinedCoach(currentCoachManger, month, year);
            }
        } else {
            Group selectedGroup = FitForLifeDataManager.getInstance().getGroupByName(groupName);
            if (month == 0)
                users = FitForLifeDataManager.getInstance().getAllJoinedByGroupAllYear(selectedGroup, year);
            else
                users = FitForLifeDataManager.getInstance().getAllJoinedByGroup(selectedGroup, month, year);
        }
        ageReportLayout.setVisibility(View.VISIBLE);
        numOfAllJoiend.setText("" + users.size());
        Log.d(TAG, "users" + users);
        Log.d(TAG, "month" + month);
        Log.d(TAG, "year" + year);
        adapter = new UserReportAdapter(this, R.layout.card_trainee_report, users, false);
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
                month = position;
                break;
            case R.id.reportYear:
                year = years.get(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private View.OnClickListener pieOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent paymentsReport = new Intent(JoinedReportActivity.this, JoinedReportChartActivity.class);
            paymentsReport.addFlags(FLAG_ACTIVITY_NEW_TASK);
            Log.d(TAG, "group " + groupName);
            Log.d(TAG, "month " + month);
            Log.d(TAG, "year " + year);
            Log.d(TAG, "isManager " + isManager);
            Log.d(TAG, "currentCoachManger " + currentCoachManger);
            paymentsReport.putExtra("Group_ID", groupName);
            paymentsReport.putExtra("Month", month);
            paymentsReport.putExtra("year", year);
            paymentsReport.putExtra("Coach_ID", currentCoachManger);
            paymentsReport.putExtra("IsManager_ID", isManager);
            startActivity(paymentsReport);
        }
    };

    /**
     * OnClickMore METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(JoinedReportActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(JoinedReportActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();
    }

} // end acitvity
