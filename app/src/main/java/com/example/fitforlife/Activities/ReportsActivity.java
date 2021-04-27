package com.example.fitforlife.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitforlife.R;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ReportsActivity extends AppCompatActivity {

    private Button paymentsReportBtn, goalsReportBtn, monthlyPaymentsReportBtn,ageReportBtn,joinedReportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);


        paymentsReportBtn = findViewById(R.id.paymentsReportButton);
        goalsReportBtn = findViewById(R.id.goalReportButton);
        ageReportBtn = findViewById(R.id.ageReportButton);
        monthlyPaymentsReportBtn = findViewById(R.id.monthlyIncomeButton);
        joinedReportBtn = findViewById(R.id.joinedReport);


        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));


        paymentsReportBtn.setOnClickListener(paymentsReportOnClick);
        goalsReportBtn.setOnClickListener(goalsReportOnClick);
        ageReportBtn.setOnClickListener(ageReportOnClick);
        monthlyPaymentsReportBtn.setOnClickListener(monthlyPaymentsReportOnClick);
        joinedReportBtn.setOnClickListener(joinedReportOnClick);
    }

    private View.OnClickListener paymentsReportOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent paymentsReport = new Intent(ReportsActivity.this, paymentsReportsActivity.class);
            startActivity(paymentsReport);
        }
    };

    private View.OnClickListener goalsReportOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent goalsReport = new Intent(ReportsActivity.this, GoalsReportActivity.class);
            startActivity(goalsReport);
        }
    };

    private View.OnClickListener joinedReportOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent joinedReport = new Intent(ReportsActivity.this, JoinedReportActivity.class);
            startActivity(joinedReport);
        }
    };
    private View.OnClickListener ageReportOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent ageReport = new Intent(ReportsActivity.this, AgeReportsActivity.class);
            startActivity(ageReport);
        }
    };
    private View.OnClickListener monthlyPaymentsReportOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent goalsReport = new Intent(ReportsActivity.this, Report_monthlyPaymentsActivity.class);
            startActivity(goalsReport);
        }
    };

    /**
     * OnClickMore METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(ReportsActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(ReportsActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }

}