package com.example.fitforlife.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Measurement;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;

import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GoalBarChartActivity extends AppCompatActivity {

    CoachInfo currentManager;
    List<CoachInfo> coaches;
    List<UserInfo> trainees;
    List<Group> groups;
    boolean isManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_bar_chart);

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        final BarChart barChart = (BarChart) findViewById(R.id.barchart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        isManager = (boolean) getIntent().getSerializableExtra("IsManager_ID");

        if (isManager) {
            currentManager = FitForLifeDataManager.getInstance().getCurrentManager();

            coaches = FitForLifeDataManager.getInstance().getAllManagerCoaches(currentManager);

            int xCounter = 0;
            for (int i = 0; i < coaches.size(); i++) {
                CoachInfo c = coaches.get(i);
                trainees = FitForLifeDataManager.getInstance().getAllCoachTrainee(c);
                if (!trainees.isEmpty()) {
                    int count = 0;
                    xCounter++;
                    for (UserInfo t : trainees) {
                        List<Measurement> weight = FitForLifeDataManager.getInstance().getTraineeMeasurement(t.getEmail());
                        Double weightGoal = t.getWeightGoal();

                        if (!weight.isEmpty()) {
                            Double weigh2 = weight.get(weight.size() - 1).getWeight();
                            Double kgLeftToLose = weigh2 - weightGoal;
                            if (kgLeftToLose <= 0)
                                count++;
                        }
                    }
                    float percentage = count * 100 / trainees.size();
                    entries.add(new BarEntry(1f * xCounter, percentage, c.getFullName() + ""));
                }
            }

        } else {
            currentManager = FitForLifeDataManager.getInstance().getCurrentCoach();
            groups = FitForLifeDataManager.getInstance().getAllCoachGroup(currentManager.getEmail());

            int xCounter = 0;
            for (int i = 0; i < groups.size(); i++) {
                Group g = groups.get(i);
                trainees = FitForLifeDataManager.getInstance().getAllGroupTrainee(g);
                if (!trainees.isEmpty()) {
                    int count = 0;
                    xCounter++;
                    for (UserInfo t : trainees) {
                        List<Measurement> weight = FitForLifeDataManager.getInstance().getTraineeMeasurement(t.getEmail());
                        Double weightGoal = t.getWeightGoal();

                        if (!weight.isEmpty()) {
                            Double weigh2 = weight.get(weight.size() - 1).getWeight();
                            Double kgLeftToLose = weigh2 - weightGoal;
                            if (kgLeftToLose <= 0)
                                count++;
                        }
                    }
                    float percentage = count * 100 / trainees.size();
                    entries.add(new BarEntry(1f * xCounter, percentage, g.getGroupName() + ""));
                }
            }
        }

        BarDataSet bardataset;
        if (isManager)
            bardataset = new BarDataSet(entries, getResources().getString(R.string.coaches));
        else
            bardataset = new BarDataSet(entries, getResources().getString(R.string.groups));

        BarData data = new BarData(bardataset);

        barChart.setData(data); // set the data and list of labels into chart
        Description description = new Description();
        if (isManager)
            description.setText(getResources().getString(R.string.goalBarMsg));
        else description.setText(getResources().getString(R.string.goalBarCoachMsg));
        description.setTextSize(12);
        description.setTextColor(getResources().getColor(R.color.neonOrange));
        barChart.setDescription(description);  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.animateY(5000);
        barChart.getBarData().setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return entry.getData().toString();
            }
        });


        barChart.getAxisLeft().setAxisMaximum(100);
        barChart.getAxisLeft().setAxisMinimum(0);

        barChart.getData().setValueTextSize(15);
        barChart.setDrawBarShadow(false);

        // Remove the grid line from background
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        barChart.getXAxis().setEnabled(false);

        // Disable the right y axis
        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);
        rightYAxis.setDrawGridLines(false);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e != null) {
                    Float value = e.getY();
                    Toast.makeText(GoalBarChartActivity.this, value + "%",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(GoalBarChartActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();
    }

    private View.OnClickListener barOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent barchart = new Intent(GoalBarChartActivity.this, GoalBarChartActivity.class);
//            barchart.addFlags(FLAG_ACTIVITY_NEW_TASK);
            // barchart.putExtra("Group_ID", coachTrainee);
            //     barchart.putExtra("Coach_ID", currentCoachManger);
            startActivity(barchart);
        }
    };
}