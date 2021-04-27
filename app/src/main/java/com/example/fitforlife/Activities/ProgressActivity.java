package com.example.fitforlife.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.anychart.AnyChartView;
import com.anychart.charts.Cartesian;
import com.example.fitforlife.Fragments.ArmsProgress_Fragment;
import com.example.fitforlife.Fragments.CWBProgress_Fragment;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Measurement;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.Fragments.ThighsProgress_Fragment;
import com.example.fitforlife.Fragments.WeightProgress_Fragment;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.mikephil.charting.charts.LineChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.data.Set;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class ProgressActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    KonfettiView konfettiView;

    private FragmentManager fm;
    private static final String TAG = "ProgressActivity";
    String[] measurements;
    Cartesian cartesian;
    private LineChart lineChart;
    AnyChartView anyChartView;
    Set set;
    List<DataEntry> seriesData;
    List<Measurement> Data;
    private Context context;
    boolean isCoach, isManager;
    private CoachInfo currentCoach = null;
    private UserInfo currentUser = null;
    private ImageView logOut;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        // Bottom Navigation Bar - action Listener
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.progress);
        setNav();

        measurements = this.getResources().getStringArray(R.array.measurements);
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, measurements);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(spinnerAdapter);
        Data = new ArrayList<>();

        if (FitForLifeDataManager.getInstance().getCurrentUser() == null && (FitForLifeDataManager.getInstance().getCurrentCoach() != null || FitForLifeDataManager.getInstance().getCurrentManager() != null)) {
            isCoach = true;

            if (FitForLifeDataManager.getInstance().getCurrentManager() != null) {
                isCoach = false;
                isManager = true;
            }
            currentUser = FitForLifeDataManager.getInstance().getCurrentUserProgress();
            Data = FitForLifeDataManager.getInstance().getTraineeMeasurement(currentUser.getEmail());
            Log.d(TAG, "traineeMeass " + FitForLifeDataManager.getInstance().getTraineeMeasurement(currentUser.getEmail()));

        }
        if (FitForLifeDataManager.getInstance().getCurrentUser() != null && FitForLifeDataManager.getInstance().getCurrentCoach() == null) {
            isCoach = false;
            isManager = false;
            currentUser = FitForLifeDataManager.getInstance().getCurrentUser();
            Data = FitForLifeDataManager.getInstance().getTraineeMeasurement(FitForLifeDataManager.getInstance().getCurrentUser().getEmail());
        }

//
//        Measurement m1 = new Measurement("12/02/20", 70.0, 70.0, 50.0, 40.0, 40.0, 30.0, 50.0, 50.0);
//        Measurement m2 = new Measurement("12/03/20", 65.0, 63.0, 49.0, 35.0, 55.0, 55.0, 60.0, 45.0);
//        Measurement m3 = new Measurement("12/04/20", 75.0, 61.0, 45.0, 34.0, 55.0, 55.0, 40.0, 45.0);
//
//        Data.add(m1);
//        Data.add(m2);
//        Data.add(m3);


        seriesData = new ArrayList<>();


        // ADDS USERS Measurement TO set
        for (Measurement tmp : Data) {
            String date = dateToString(tmp.getDate());
            Log.d(TAG, "String date " + date);
            seriesData.add(new CustomDataEntry(date, tmp.getWeight(), tmp.getWaist(), tmp.getButtocks(), tmp.getChest(), tmp.getLeftArm(), tmp.getRightArm(), tmp.getLeftThigh(), tmp.getRightThigh()));
        }

    }


    public void showConfetti() {
        konfettiView = findViewById(R.id.viewKonfetti);
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);
    }

    private String dateToString(long timeInMillis) {
        Date currentDate = new Date(timeInMillis);
        DateFormat df = new SimpleDateFormat("dd/M/yyyy");

        String date = "" + (df.format(currentDate));

        return date;
    }


    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2, Number value3, Number value4, Number value5, Number value6, Number value7, Number value8) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
            setValue("value4", value4);
            setValue("value5", value5);
            setValue("value6", value6);
            setValue("value7", value7);
            setValue("value8", value8);
        }
    }


    public List<DataEntry> getMyData() {

        return seriesData;
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (measurements[position]) {

            case "Weight":
                if (!isCoach && !isManager) {
                    WeightProgress_Fragment myEventsFragment = new WeightProgress_Fragment(currentUser, false);
                    fragmentManager.beginTransaction().add(R.id.content_frame, myEventsFragment).attach(myEventsFragment).commit();
                    break;
                }
                if (isCoach || isManager) {
                    WeightProgress_Fragment myEventsFragment = new WeightProgress_Fragment(currentUser, true);
                    fragmentManager.beginTransaction().add(R.id.content_frame, myEventsFragment).attach(myEventsFragment).commit();
                    break;
                }

            case "Arms":
                ArmsProgress_Fragment armsFragment = new ArmsProgress_Fragment();
                fragmentManager.beginTransaction().replace(R.id.content_frame, armsFragment).commit();
                break;
            case "Thighs":
                ThighsProgress_Fragment thighFragment = new ThighsProgress_Fragment();
                fragmentManager.beginTransaction().replace(R.id.content_frame, thighFragment).commit();
                break;
            case "Waist ,Chest And Buttocks":
                CWBProgress_Fragment threeFragment = new CWBProgress_Fragment();
                fragmentManager.beginTransaction().replace(R.id.content_frame, threeFragment).commit();
                break;
            default:
        }
    }

    /**
     * logout method
     *
     * @param view - logout icon
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(ProgressActivity.this, MainActivity.class);
        startActivity(MoreIntent);

    }

    /**
     * More METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(ProgressActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    private void setNav() {
        if (isCoach || isManager)
            bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calendar:
                        if (isManager) {
                            Intent calendarIntent = new Intent(ProgressActivity.this, ManagerCalendarAcitvity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent calendarIntent = new Intent(ProgressActivity.this, CalendarActivity.class);
                            startActivity(calendarIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.progress:
                        if (isCoach || isManager) {
                            Intent postIntent = new Intent(ProgressActivity.this, TraineeProgressActivity.class);
                            startActivity(postIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent ProgressIntent = new Intent(ProgressActivity.this, ProgressActivity.class);
                            startActivity(ProgressIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.home:
                        Intent homeIntent = new Intent(ProgressActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.payments:
                        Intent paymentsIntent;
                        if (isCoach || isManager) {
                            paymentsIntent = new Intent(ProgressActivity.this, TraineePaymentsActivity.class);
                        } else {
                            paymentsIntent = new Intent(ProgressActivity.this, PaymentsActivity.class);
                        }
                        startActivity(paymentsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        if (isManager) {
                            Intent groupsIntent = new Intent(ProgressActivity.this, ManagerCoachTraineeGroupsActivity.class);
                            startActivity(groupsIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                        if (isCoach) {
                            Intent groupsIntent = new Intent(ProgressActivity.this, GroupsTraineeActivity.class);
                            startActivity(groupsIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent profileIntent = new Intent(ProgressActivity.this, ProfileActivity.class);
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

}