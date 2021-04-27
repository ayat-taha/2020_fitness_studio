package com.example.fitforlife.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.anychart.palettes.RangeColors;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Report_MonthlyPaymentChartActivity extends AppCompatActivity {
    private static final String TAG = "MonthlyPaymentChart";

    AnyChartView anyChartView;
    List<DataEntry> seriesData;
    List<UserInfo> Data;
    String groupName, method;
    int year, month;
    private CoachInfo currentCoachManger;
    boolean isManager;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report__monthly_payment_chart);

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        anyChartView = findViewById(R.id.any_chart_view_joined);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));
        seriesData = new ArrayList<>();
        //get extra
        groupName = (String) getIntent().getSerializableExtra("Group_ID");
        month = (int) getIntent().getSerializableExtra("Month");
        year = (int) getIntent().getSerializableExtra("year");
        method = (String) getIntent().getSerializableExtra("method");
        isManager = (boolean) getIntent().getSerializableExtra("IsManager_ID");
        if (isManager)
            currentCoachManger = FitForLifeDataManager.getInstance().getCurrentManager();
        else currentCoachManger = FitForLifeDataManager.getInstance().getCurrentCoach();
        Log.d(TAG, "groupName: " + groupName);
        Log.d(TAG, "month: " + month);
        Log.d(TAG, "year: " + year);
        Log.d(TAG, "currentCoachManger: " + method);
        Log.d(TAG, "isManager: " + isManager);

        title = findViewById(R.id.chartGroupName);
        if (groupName.equals("All groups"))
            title.setText(getResources().getString(R.string.totalPaymentIn) + " " + year + " " + getResources().getString(R.string.method) + ": " + method);
        else
            title.setText(getResources().getString(R.string.totalPaymentsFor) + " " + groupName + " " + getResources().getString(R.string.in) + " " + year + " " + getResources().getString(R.string.method) + ": " + method);
        if (!isManager) {
            int monthlySum = 0;
            for (int i = 1; i <= 12; i++) {
                monthlySum = FitForLifeDataManager.getInstance().getMonthlySumReport(i, year, groupName, method, currentCoachManger.getEmail());
                seriesData.add(new Report_MonthlyPaymentChartActivity.CustomDataEntry(i + "/" + year, monthlySum));
                Log.d("MonthlyPaymentChart", "OnClickSearch: monthily sum " + monthlySum);

            }

        } else {
            Group group = FitForLifeDataManager.getInstance().getGroupByName(groupName);
            for (int i = 1; i <= 12; i++) {
                int monthlySum = 0;
                monthlySum = FitForLifeDataManager.getInstance().getManagerMonthlySumReport(i, year, groupName, method, group.getCoachId());
                seriesData.add(new Report_MonthlyPaymentChartActivity.CustomDataEntry(i + "/" + year, monthlySum));
                Log.d("MonthlyPaymentChart", "OnClickSearch: monthily sum " + monthlySum);

            }
        }
        Cartesian cartesian = AnyChart.line();
        RangeColors palette = RangeColors.instantiate();
        palette.items("#6f00ff", "#cc00ff");
        cartesian.animation(true);
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
//        cartesian.title("Sum");

        cartesian.yAxis(0).title(getResources().getString(R.string.sumPayment));
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping seriesMapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series1 = cartesian.line(seriesMapping);
        series1.name(getResources().getString(R.string.sum));
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);
        cartesian.background().fill("#FFFFFFFF");
        anyChartView.setChart(cartesian);


    }

    private String dateToString(long timeInMillis) {
        Date currentDate = new Date(timeInMillis);
        DateFormat df = new SimpleDateFormat("M/yyyy");

        String date = "" + (df.format(currentDate));
        Log.d(TAG, "dateToString: " + date);
        return date;
    }

    private String dateToString2(long timeInMillis) {
        Date currentDate = new Date(timeInMillis);
        DateFormat df = new SimpleDateFormat("dd/M/yyyy");

        String date = "" + (df.format(currentDate));
        Log.d(TAG, "dateToString: " + date);
        return date;
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value) {
            super(x, value);

        }
    }

    /**
     * OnClickMore METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(Report_MonthlyPaymentChartActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(Report_MonthlyPaymentChartActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();
    }
}
