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

import com.example.fitforlife.Adapters.UserPaymentAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Report_monthlyPaymentsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "monthlyPaymentsActivity";

    List<UserInfo> users;
    List<Group> groupsTable;
    private CoachInfo currentCoach, currentManager;
    boolean isCoach, isManager;
    String groupName, method;
    int year, month, monthlySum;
    ArrayList<String> groupsNames;
    ArrayList<String> methodsArr;
    ArrayList<String> methodsArrEn;
    String[] groupNamesArr;
    private String[] months = new String[12];
    private String[] methods = new String[5];
    private String[] methodsEn = new String[5];
    List<Integer> years;
    UserPaymentAdapter adapter;
    ListView traineeListView;
    EditText inputSearch;
    ImageView pieChartBtn;

    TextView monthlySumTxtView;
    Map<UserInfo, Integer> monthlyUsersSum = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_monthly_payments);
        currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
        currentManager = FitForLifeDataManager.getInstance().getCurrentManager();
        if (currentCoach == null && currentManager != null) {
            isManager = true;
            isCoach = false;
        } else {
            isCoach = true;
            isManager = false;
        }
        pieChartBtn = findViewById(R.id.pieChartBtn);
        pieChartBtn.setOnClickListener(pieOnClick);
        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        users = FitForLifeDataManager.getInstance().getAllCoachTrainee(currentCoach);
        traineeListView = findViewById(R.id.listForPaymentsReport);
        monthlySumTxtView = findViewById(R.id.monthlySum);

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

        //method spinner
        Spinner methodSpin = (Spinner) findViewById(R.id.reportMethod);
        methodsArr = new ArrayList<>();
        methodsArr.add(getResources().getString(R.string.allMethods));

        methodsArrEn = new ArrayList<>();
        methodsArrEn.add(getResources().getString(R.string.allMethods));

        methods = getResources().getStringArray(R.array.payment_methods);
        for (String m : methods)
            methodsArr.add(m);

        methodsEn = getResources().getStringArray(R.array.payment_methodsEn);
        for (String m : methodsEn)
            methodsArrEn.add(m);
        methodSpin.setSelection(0);
        ArrayAdapter spinnerMethodAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, methodsArr);
        spinnerMethodAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        methodSpin.setAdapter(spinnerMethodAdapter); //Setting the ArrayAdapter data on the Spinner
        methodSpin.setOnItemSelectedListener(this);
    }

    public void OnClickSearch(View view) {

        if (isCoach) {
            monthlyUsersSum = FitForLifeDataManager.getInstance().getMonthlyUserReport(month, year, groupName, method, currentCoach.getEmail());
            monthlySum = FitForLifeDataManager.getInstance().getMonthlySumReport(month, year, groupName, method, currentCoach.getEmail());

        } else {
            Group group = FitForLifeDataManager.getInstance().getGroupByName(groupName);
            monthlyUsersSum = FitForLifeDataManager.getInstance().getManagerMonthlyUserReport(month, year, groupName, method, group.getCoachId());
            Log.d("kkkkk", "OnClickSearch:  uer monthily sum " + monthlyUsersSum);
            monthlySum = FitForLifeDataManager.getInstance().getManagerMonthlySumReport(month, year, groupName, method, group.getCoachId());
            Log.d("kkkkk", "OnClickSearch: monthily sum " + monthlySum);

        }
        monthlySumTxtView.setText(monthlySum + " ");
        //   if (groupName.equals(this.getResources().getString(R.string.allGroups))) {

//            users = FitForLifeDataManager.getInstance().getMonthlyPaymentsReport(month, year);
//            for (UserInfo u : users) {
//                if (!groupsTable.contains(u.getGroupId()))
//                    users.remove(u);
//            }
//        } else {
//            Group group = FitForLifeDataManager.getInstance().getGroupByName(groupName);
//            users = FitForLifeDataManager.getInstance().getGroupPaidReport(group, month, year);
//        }
        adapter = new UserPaymentAdapter(this, (LinkedHashMap<UserInfo, Integer>) monthlyUsersSum);
        traineeListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long arg3) {
        int id = parent.getId();
        switch (id) {
            case R.id.reportGroup:
                groupName = groupNamesArr[position];
                break;
            case R.id.reportMethod:
                method = methodsArrEn.get(position);
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

    private View.OnClickListener pieOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent paymentsReport = new Intent(Report_monthlyPaymentsActivity.this, Report_MonthlyPaymentChartActivity.class);
            paymentsReport.addFlags(FLAG_ACTIVITY_NEW_TASK);
            Log.d(TAG, "group " + groupName);
            Log.d(TAG, "month " + month);
            Log.d(TAG, "year " + year);
            Log.d(TAG, "isManager " + isManager);
            paymentsReport.putExtra("Group_ID", groupName);
            paymentsReport.putExtra("Month", month);
            paymentsReport.putExtra("year", year);
            paymentsReport.putExtra("method", method);
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
        Intent MoreIntent = new Intent(Report_monthlyPaymentsActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(Report_monthlyPaymentsActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();
    }
}