package com.example.fitforlife.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

public class AgeReportPieActivity extends AppCompatActivity {

    // Create the object of TextView
    // and PieChart class
    TextView tv13, tv13_18, tv19_30, tv30, tvNumOf13, tvNumOf13_18, tvNumOf19_30, tvNumOf30, groupName;
    PieChart pieChart;
    Group group;
    List<UserInfo> Trainees = new ArrayList<UserInfo>();
    List<UserInfo> Trainees13 = new ArrayList<UserInfo>();
    List<UserInfo> Trainees13_18 = new ArrayList<UserInfo>();
    List<UserInfo> Trainees19_30 = new ArrayList<UserInfo>();
    List<UserInfo> Trainees30 = new ArrayList<UserInfo>();
    boolean isManager;
    private CoachInfo currentCoachManger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_report_pie);

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        // we have given in .XML file
        tv13 = findViewById(R.id.tvR);
        tv13_18 = findViewById(R.id.tvPython);
        tv19_30 = findViewById(R.id.tvCPP);
        tv30 = findViewById(R.id.tvJava);

        tvNumOf13 = findViewById(R.id.numOf13);
        tvNumOf13_18 = findViewById(R.id.numOf13_18);
        tvNumOf19_30 = findViewById(R.id.numOf19_30);
        tvNumOf30 = findViewById(R.id.numOf30);

        groupName = findViewById(R.id.pieGroupName);

        pieChart = findViewById(R.id.piechart);

        group = (Group) getIntent().getSerializableExtra("Group_ID");
        groupName.setText(group.getGroupName());

        currentCoachManger = (CoachInfo) getIntent().getSerializableExtra("Coach_ID");
        isManager = (boolean) getIntent().getSerializableExtra("IsManager_ID");
        if (isManager) {
            Trainees = FitForLifeDataManager.getInstance().getAllAgeGroupTrainee(group, getResources().getString(R.string.allAges));
            Trainees13 = FitForLifeDataManager.getInstance().getAllAgeGroupTrainee(group, getResources().getString(R.string._13));
            Trainees13_18 = FitForLifeDataManager.getInstance().getAllAgeGroupTrainee(group, getResources().getString(R.string._13_18));
            Trainees19_30 = FitForLifeDataManager.getInstance().getAllAgeGroupTrainee(group, getResources().getString(R.string._19_30));
            Trainees30 = FitForLifeDataManager.getInstance().getAllAgeGroupTrainee(group, getResources().getString(R.string._30));
        } else {
            Trainees = FitForLifeDataManager.getInstance().getAllAgeCoachTrainee(group, getResources().getString(R.string.allAges), currentCoachManger);
            Trainees13 = FitForLifeDataManager.getInstance().getAllAgeCoachTrainee(group, getResources().getString(R.string._13), currentCoachManger);
            Trainees13_18 = FitForLifeDataManager.getInstance().getAllAgeCoachTrainee(group, getResources().getString(R.string._13_18), currentCoachManger);
            Trainees19_30 = FitForLifeDataManager.getInstance().getAllAgeCoachTrainee(group, getResources().getString(R.string._19_30), currentCoachManger);
            Trainees30 = FitForLifeDataManager.getInstance().getAllAgeCoachTrainee(group, getResources().getString(R.string._30), currentCoachManger);
        }

        // Creating a method setData()
        // to set the text in text view and pie chart
        setData();
    }

    private void setData() {

        // Set the percentage of language used
        tv13.setText(Float.toString((Trainees13.size() * 100 / Trainees.size())));
        tv13_18.setText(Float.toString(Trainees13_18.size() * 100 / Trainees.size()));
        tv19_30.setText(Float.toString(Trainees19_30.size() * 100 / Trainees.size()));
        tv30.setText(Float.toString(Trainees30.size() * 100 / Trainees.size()));

        tvNumOf13.setText(Integer.toString(Trainees13.size()));
        tvNumOf13_18.setText(Integer.toString(Trainees13_18.size()));
        tvNumOf19_30.setText(Integer.toString(Trainees19_30.size()));
        tvNumOf30.setText(Integer.toString(Trainees30.size()));

        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string._13),
                        Float.parseFloat(tv13.getText().toString()),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string._13_18),
                        Float.parseFloat(tv13_18.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string._19_30),
                        Float.parseFloat(tv19_30.getText().toString()),
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        getResources().getString(R.string._30),
                        Float.parseFloat(tv30.getText().toString()),
                        Color.parseColor("#29B6F6")));

        // To animate the pie chart
        pieChart.startAnimation();
    }

    /**
     * OnClickMore METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(com.example.fitforlife.Activities.AgeReportPieActivity.this, HomeActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(com.example.fitforlife.Activities.AgeReportPieActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();
    }
}