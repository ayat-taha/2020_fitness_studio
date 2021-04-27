package com.example.fitforlife.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.example.fitforlife.Adapters.UserAgeReportAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AgeReportsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private boolean isManager;
    private CoachInfo currentCoachManger;
    ListView traineeListView;
    private UserAgeReportAdapter adapter;
    List<UserInfo> coachTrainee;
    List<Group> groupsTable;
    String[] ageTable;
    List<String> ageTableList = new ArrayList<>();
    EditText inputSearch;
    Group selectedGroup;
    String selectedAge;

    ImageView pieChartBtn;
    TextView numOfAge, numOfAllAge;
    LinearLayout ageReportLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_reports);
        // setting custom action bar
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
        traineeListView = findViewById(R.id.listForAgeReport);
        pieChartBtn = findViewById(R.id.pieChartBtn);
        pieChartBtn.setOnClickListener(pieOnClick);
        numOfAge = findViewById(R.id.numOfAge);
        numOfAllAge = findViewById(R.id.numOfAllAge);
        ageReportLayout = findViewById(R.id.ageReportLayout);

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
        Spinner GroupSpin = (Spinner) findViewById(R.id.reportAgeGroupSpinner);
        if (!isManager) {
            groupsTable = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoachManger.getEmail());
            coachTrainee = FitForLifeDataManager.getInstance().getAllCoachTrainee(currentCoachManger);

        } else if (isManager) {
            groupsTable = FitForLifeDataManager.getInstance().getAllManagerGroups(currentCoachManger);
            coachTrainee = FitForLifeDataManager.getInstance().getAllManagerTrainee(currentCoachManger);
        }


        Group tmp = new Group();
        tmp.setGroupName(this.getResources().getString(R.string.allTrainee));
        groupsTable.add(0, tmp);
        ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(this, R.layout.simple_spinner_item, groupsTable);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        GroupSpin.setAdapter(adapter);
        GroupSpin.setSelection(0); //set selection to all groups
        GroupSpin.setOnItemSelectedListener(this);

        Spinner AgeSpin = (Spinner) findViewById(R.id.reportAgeSpinner);
        ageTable = getResources().getStringArray(R.array.ageRange);
        ageTableList.add(getResources().getString(R.string.allAges));
        ageTableList.addAll(Arrays.asList(ageTable));
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.simple_spinner_item, ageTableList);
        adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        AgeSpin.setAdapter(adapter2);
        AgeSpin.setSelection(0); //set selection to all ages
        AgeSpin.setOnItemSelectedListener(this);

    } // END ON CREATE


    @Override
    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {

        Spinner spinner = (Spinner) parent;

        int spinId = spinner.getId();
        switch (spinId) {
            case R.id.reportAgeSpinner:
                if (spinner.getSelectedItemPosition() == 0) {
                    pieChartBtn.setVisibility(View.VISIBLE);
                    ageReportLayout.setVisibility(View.GONE);
                } else {
                    pieChartBtn.setVisibility(View.GONE);
                    ageReportLayout.setVisibility(View.VISIBLE);
                }
                selectedAge = ageTableList.get(spinner.getSelectedItemPosition());

                break;
            case R.id.reportAgeGroupSpinner:
                selectedGroup = groupsTable.get(spinner.getSelectedItemPosition());

                break;
        }
        List<UserInfo> groupCoachTrainee = new ArrayList<UserInfo>();
        List<UserInfo> allGroupCoachTrainee = new ArrayList<UserInfo>();

        if (isManager) {
            groupCoachTrainee = FitForLifeDataManager.getInstance().getAllAgeGroupTrainee(selectedGroup, selectedAge);
            allGroupCoachTrainee = FitForLifeDataManager.getInstance().getAllAgeGroupTrainee(selectedGroup, getResources().getString(R.string.allAges));
        } else {
            groupCoachTrainee = FitForLifeDataManager.getInstance().getAllAgeCoachTrainee(selectedGroup, selectedAge, currentCoachManger);
            allGroupCoachTrainee = FitForLifeDataManager.getInstance().getAllAgeCoachTrainee(selectedGroup, getResources().getString(R.string.allAges), currentCoachManger);
        }
        if (ageReportLayout.getVisibility() == View.VISIBLE) {
            numOfAge.setText(groupCoachTrainee.size() + "");
            numOfAllAge.setText(allGroupCoachTrainee.size() + "");
        }
        adapter = new UserAgeReportAdapter(this, R.layout.card_trainee_age_report, groupCoachTrainee);
        traineeListView.setAdapter(adapter);

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private View.OnClickListener pieOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent paymentsReport = new Intent(AgeReportsActivity.this, AgeReportPieActivity.class);
            paymentsReport.addFlags(FLAG_ACTIVITY_NEW_TASK);
            paymentsReport.putExtra("Group_ID", selectedGroup);
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
        Intent MoreIntent = new Intent(com.example.fitforlife.Activities.AgeReportsActivity.this, HomeActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(com.example.fitforlife.Activities.AgeReportsActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();
    }
} // END ACTIVITY
