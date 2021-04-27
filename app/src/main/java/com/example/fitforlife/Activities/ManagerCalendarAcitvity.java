package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitforlife.Adapters.ManagerEventAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManagerCalendarAcitvity extends AppCompatActivity {
    private static final String TAG = "ManagerCalendarAcitvity";

    BottomNavigationView bottomNavigationView;
    private ListView eventList;

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView monthYear;
    Date today;
    CompactCalendarView compactCalendar;


    private Group currentGroup;
    private List<Session> sessionList;
    List<Event> canceledEventsList;
    private ManagerEventAdapter adapter;
    boolean isCoach;
    int absentCountThisMonth;
    private List<Group> coachGroups;

    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    public void deleteCanceled(Event currentEvent) {
        compactCalendar.removeEvent(currentEvent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_calendar_acitvity);

        // Bottom Navigation Bar - action Listener
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.calendar);
        setNav();
        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        //

        eventList = findViewById(R.id.managerEventList);
        // set title for calendar
        final Date date = new Date();
        monthYear = findViewById(R.id.managerMonthYear);
        monthYear.setText(dateFormatMonth.format(date));
        CoachInfo CurrentManager = FitForLifeDataManager.getInstance().getCurrentManager();
        sessionList = FitForLifeDataManager.getInstance().getAllManagerSessions(CurrentManager);
        compactCalendar = findViewById(R.id.ManagerCalendar);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        onClickCal();


        Log.d(TAG, "MANAGER SESSIONS " + sessionList);
        findDays(date);


    }

    private void findCanceled(Date date) {

        List<Event> event = FitForLifeDataManager.getInstance().getAllManagerCanceled(FitForLifeDataManager.getInstance().getCurrentManager());
        Log.d(TAG, "findCanceled: " + event);
        compactCalendar.removeEvents(event);
    }

    private void findRes(Date date) {
        List<Event> calendarEvents = compactCalendar.getEvents(date);
        List<Event> event = FitForLifeDataManager.getInstance().getAllManagerReschedule(FitForLifeDataManager.getInstance().getCurrentManager());
        for (Event tmp : event) {
            if (!calendarEvents.contains(tmp)) {
                Log.d(TAG, "findRes :  " + tmp);
                compactCalendar.addEvent(tmp);

            }
        }
    }

    public void onClickCal() {
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = ManagerCalendarAcitvity.this;

                adapter = new ManagerEventAdapter(context, R.layout.manager_card_events, compactCalendar.getEvents(dateClicked));
                eventList.setAdapter(adapter);

                if (compactCalendar.getEvents(dateClicked).isEmpty()) {
                    Toast.makeText(context, "No Events Planned for that day", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "there is " + compactCalendar.getEvents(dateClicked).size() + " Event Today", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthYear.setText(dateFormatMonth.format(firstDayOfNewMonth));
                compactCalendar.removeAllEvents();
                findDays(firstDayOfNewMonth);
            }
        });
    }

    /**
     * Fill Callendar with current Sessions
     *
     * @param month - current displayed month
     */
    public void findDays(final Date month) {
        Event ev1;
        today = new Date();
        for (Session tmpSession : sessionList) {
            int i = findweekDay(tmpSession.getDay());
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, i);
            cal.set(Calendar.HOUR_OF_DAY, tmpSession.getHour());
            cal.set(Calendar.MINUTE, tmpSession.getMinute());
            cal.set(Calendar.SECOND, 00);
            cal.set(Calendar.MONTH, month.getMonth());
            cal.set(Calendar.MILLISECOND, 00);
            cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
            cal.set(Calendar.YEAR, month.getYear() + 1900);
            int currentMonth = cal.get(Calendar.MONTH);
            while (cal.get(Calendar.MONTH) == currentMonth) {
                ///loop not going list
                //if user2 null-yellow
                //if user2 != current user - ignore
                //if user2 == current user - red


                if (cal.getTimeInMillis() - today.getTime() < 0) {
                    ev1 = new Event(Color.RED, cal.getTimeInMillis(), tmpSession);
                } else {
                    ev1 = new Event(Color.GREEN, cal.getTimeInMillis(), tmpSession);
                }


                compactCalendar.addEvent(ev1);
                cal.add(Calendar.DAY_OF_MONTH, 7);

            }


        }

        findCanceled(new Date());
        findRes(new Date());
    }


    private int findweekDay(String day) {
        int i = 1;
        switch (day) {
            case "Sunday":
                i = 1;
                break;
            case "Monday":
                i = 2;
                break;
            case "Tuesday":
                i = 3;
                break;
            case "Wednesday":
                i = 4;
                break;
            case "Thursday":
                i = 5;
                break;
            case "Friday":
                i = 6;
                break;
            case "Saturday":
                i = 7;
                break;
        }
        return i;
    }

    /**
     * OnClickMore
     *
     * @param view - imageview
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(ManagerCalendarAcitvity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * LOGOUT METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(ManagerCalendarAcitvity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();


    }


    private void setNav() {
        bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calendar:
                        Intent calendarIntent = new Intent(ManagerCalendarAcitvity.this, ManagerCalendarAcitvity.class);
                        startActivity(calendarIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.progress:
                        Intent Progress = new Intent(ManagerCalendarAcitvity.this, TraineeProgressActivity.class);
                        startActivity(Progress);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.home:
                        Intent postIntent = new Intent(ManagerCalendarAcitvity.this, HomeActivity.class);
                        startActivity(postIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.payments:
                        Intent paymentsIntent;
                        paymentsIntent = new Intent(ManagerCalendarAcitvity.this, TraineePaymentsActivity.class);
                        startActivity(paymentsIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.profile:
                        Intent groupsIntent = new Intent(ManagerCalendarAcitvity.this, ManagerCoachTraineeGroupsActivity.class);
                        startActivity(groupsIntent);
                        overridePendingTransition(0, 0);
                        return true;


                }
                return false;
            }
        });
        // END NAVIGATION BAR
    }

}//end activity
