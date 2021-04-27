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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fitforlife.Adapters.TraineeAdapter;
import com.example.fitforlife.Adapters.UserGoalReportAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Measurement;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class GoalsReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private boolean isManager;
    private CoachInfo currentCoachManger;
    ListView traineeListView;
    private UserGoalReportAdapter adapter;
    List<UserInfo> coachTrainee;
    List<Group> groupsTable;
    EditText inputSearch;
    TextView reachedPercentage;
    ImageView barChartBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_report);
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
        traineeListView = findViewById(R.id.listForGoalReport);

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
        Spinner GroupSpin = (Spinner) findViewById(R.id.reportGoalGroupSpinner);
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

        reachedPercentage = findViewById(R.id.reachedPercentage);

        barChartBtn = findViewById(R.id.barChartBtn);
        barChartBtn.setOnClickListener(barOnClick);
//        if (isManager)
//            barChartBtn.setVisibility(View.VISIBLE);

    } // END ON CREATE


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        Group selectedGroup = groupsTable.get(position);
        if (position == 0) {
            if (!coachTrainee.isEmpty()) {
                adapter = new UserGoalReportAdapter(this, R.layout.card_trainee_goal_report, coachTrainee);
                traineeListView.setAdapter(adapter);
                int count = 0;
                for (int i = 0; i < coachTrainee.size(); i++) {
                    List<Measurement> weight = FitForLifeDataManager.getInstance().getTraineeMeasurement(coachTrainee.get(i).getEmail());
                    Double weightGoal = coachTrainee.get(i).getWeightGoal();

                    if (!weight.isEmpty()) {
                        Double weigh2 = weight.get(weight.size() - 1).getWeight();
                        Double kgLeftToLose = weigh2 - weightGoal;
                        if (kgLeftToLose <= 0) {
                            count++;
                        }
                    }
                }
                Log.d("goalsreportcount   ", count + "");
                if (coachTrainee.size() != 0)
                    reachedPercentage.setText(Float.toString((count * 100 / coachTrainee.size())));
                else reachedPercentage.setText("0");
            }
        } else {
            List<UserInfo> groupCoachTrainee = new ArrayList<UserInfo>();
            groupCoachTrainee = FitForLifeDataManager.getInstance().getAllGroupTrainee(selectedGroup);
            adapter = new UserGoalReportAdapter(this, R.layout.card_trainee_goal_report, groupCoachTrainee);
            traineeListView.setAdapter(adapter);
            int count = 0;
            for (int i = 0; i < groupCoachTrainee.size(); i++) {
                List<Measurement> weight = FitForLifeDataManager.getInstance().getTraineeMeasurement(groupCoachTrainee.get(i).getEmail());
                Double weightGoal = groupCoachTrainee.get(i).getWeightGoal();

                if (!weight.isEmpty()) {
                    Double weigh2 = weight.get(weight.size() - 1).getWeight();
                    Double kgLeftToLose = weigh2 - weightGoal;
                    if (kgLeftToLose <= 0) {
                        count++;
                    }
                }
            }
            Log.d("goalsreportcount   ", count + "");
            if (groupCoachTrainee.size() != 0)
                reachedPercentage.setText(Float.toString((count * 100 / groupCoachTrainee.size())));
            else reachedPercentage.setText("0");
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
        Intent MoreIntent = new Intent(GoalsReportActivity.this, HomeActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(GoalsReportActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();
    }

    private View.OnClickListener barOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent barchart = new Intent(GoalsReportActivity.this, GoalBarChartActivity.class);
            barchart.putExtra("IsManager_ID", isManager);
            startActivity(barchart);
        }
    };
} // END ACTIVITY
