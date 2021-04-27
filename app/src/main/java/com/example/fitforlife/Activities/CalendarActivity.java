package com.example.fitforlife.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitforlife.Adapters.EventArrayAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "Calendar";

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private ListView eventList;
    TextView monthYear;
    Date today;
    CompactCalendarView compactCalendar;
    BottomNavigationView bottomNavigationView;


    private Group currentGroup;
    private List<Session> sessionList;
    List<Event> canceledEventsList;
    private EventArrayAdapter adapter;
    boolean isCoach;
    int absentCountThisMonth;
    private CoachInfo currentCoach;
    private List<Group> coachGroups;
    private UserInfo currentUser;

    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Bottom Navigation Bar - action Listener
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.calendar);
        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        //list
        eventList = findViewById(R.id.eventList);

        // set title for calendar
        final Date date = new Date();
        monthYear = findViewById(R.id.monthYear);
        monthYear.setText(dateFormatMonth.format(date));

        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        onClickCal();
        currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
        currentUser = FitForLifeDataManager.getInstance().getCurrentUser();
        canceledEventsList = new ArrayList<>();

        //CURRENT USER IS ****** TRAINEEE *******
        if (currentUser != null && currentCoach == null) {
            sessionList = new ArrayList<>();
            isCoach = false;
            absentCountThisMonth = 0;

            if (currentUser.getGroupId() != null) {
                currentGroup = FitForLifeDataManager.getInstance().getGroup(currentUser.getGroupId());
                sessionList = FitForLifeDataManager.getInstance().getAllGroupSessions(currentGroup);
                Date date2 = new Date();
                findDays(date2);


            }
        }


        //CURRENT USER IS ****** COACH *******
        else if (currentCoach != null && currentUser == null) {
            isCoach = true;
            bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);
            List<Session> tmpSessions = new ArrayList<>();
            coachGroups = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
            for (Group tmpGroup : coachGroups) {
                tmpSessions.addAll(FitForLifeDataManager.getInstance().getAllGroupSessions(tmpGroup));
            }
            sessionList = tmpSessions;
            Date date2 = new Date();
            findDays(date2);

        }


        setNav();


    }


    public void onClickCal() {
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = CalendarActivity.this;

                adapter = new EventArrayAdapter(context, R.layout.card_events, compactCalendar.getEvents(dateClicked), isCoach, false);
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


    //    /**
//     * TAKE DAYS + TIME  FROM USER'S GROUP (ADD LOOP ON SET )
//     */
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

        checkIfCanceled(month);
        if (!isCoach) {
            findResUser(currentGroup);
            findNotGoing(month);
            User2isCurrentUser(month);

        }
        if(isCoach)
            findResCoach(coachGroups);


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

    private void findResCoach(List<Group> coachGroups) {
        List<Event> resEvents = FitForLifeDataManager.getInstance().getAllRescheduledSessions();
        for (Event tmp : resEvents) {
            Group group = FitForLifeDataManager.getInstance().getGroup(((Session) tmp.getData()).getGroupId());
            if (coachGroups.contains(group)) {
                compactCalendar.addEvent(tmp);
            }

        }
    }

    public void checkIfCanceled(Date month) {
        canceledEventsList = FitForLifeDataManager.getInstance().getAllCanceledSessions();
        for (Event tmp : canceledEventsList) {
            compactCalendar.removeEvent(tmp);

        }
    }


    private void findNotGoing(Date month) {
        List<Event> notGoingList = FitForLifeDataManager.getInstance().getAllNotGoingForUser(currentUser.getEmail());
        for (Event event : compactCalendar.getEventsForMonth(month)) {
            for (Event tmp : notGoingList) {
                if (tmp.getTimeInMillis() == event.getTimeInMillis() && ((Session) tmp.getData()).getGroupId().equals(((Session) event.getData()).getGroupId()))
                    compactCalendar.removeEvent(event);
            }
        }
        findFirstLastDate(month);
    }


    private void findResUser(Group userGroup) {
        List<Event> resEvents = FitForLifeDataManager.getInstance().getAllRescheduledSessions();

        for (Event tmp : resEvents) {
            if (((Session) tmp.getData()).getGroupId().equals(userGroup.getGroupId())) {
                compactCalendar.addEvent(tmp);
            }
        }

    }

    public void userMissingSessionThisMonth(long firstDay, long lastDay) {

        int count1 = FitForLifeDataManager.getInstance().getNotGoingCountThisMonth(firstDay, lastDay, currentUser.getEmail());
        int count2 = FitForLifeDataManager.getInstance().User2isCurrentUser(firstDay, lastDay, currentUser.getEmail()).size();
        Log.d(TAG, "not going this month" + count1);
        Log.d(TAG, "user 2 is nfsu " + count2);
        if (count1 - count2 > 0)
            findAvailable(firstDay, lastDay);
        else
            deleteAvailable(firstDay, lastDay);
    }

    private void deleteAvailable(long firstDay, long lastDay) {

        List<Event> available = FitForLifeDataManager.getInstance().getAvailableThisMonthList(firstDay, lastDay, currentUser.getEmail());
        compactCalendar.removeEvents(available);


    }

    private void findAvailable(long firstDay, long lastDay) {

        List<Event> available = FitForLifeDataManager.getInstance().getAvailableThisMonthList(firstDay, lastDay, currentUser.getEmail());

        compactCalendar.addEvents(available);
        Log.d(TAG, "available " + available);


    }

    private void findFirstLastDate(Date date) {

        // find first day of current open month
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(date);

//        firstDay.add(Calendar.MONTH, 1);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
//        firstDay.add(Calendar.DATE, -1);
        firstDay.set(Calendar.HOUR_OF_DAY, 00);
        firstDay.set(Calendar.MINUTE, 01);
        firstDay.set(Calendar.SECOND, 00);
        firstDay.set(Calendar.MILLISECOND, 00);
        Log.d(TAG, "the Date " + date + "timeinmillies  first day " + firstDay.getTimeInMillis());


        // find last day of current open month
        Calendar lastDay = Calendar.getInstance();
        lastDay.setTime(date);

        lastDay.add(Calendar.MONTH, 1);
        lastDay.set(Calendar.DAY_OF_MONTH, 1);
        lastDay.add(Calendar.DATE, -1);
        lastDay.set(Calendar.HOUR_OF_DAY, 23);
        lastDay.set(Calendar.MINUTE, 59);
        lastDay.set(Calendar.SECOND, 00);
        lastDay.set(Calendar.MILLISECOND, 00);
        Log.d(TAG, "the Date " + date + "Timeinmillies  last day " + lastDay.getTimeInMillis());


        userMissingSessionThisMonth(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }


    private void User2isCurrentUser(Date date) {
        // find first day of current open month
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(date);

//        firstDay.add(Calendar.MONTH, 1);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
//        firstDay.add(Calendar.DATE, -1);
        firstDay.set(Calendar.HOUR_OF_DAY, 00);
        firstDay.set(Calendar.MINUTE, 01);
        firstDay.set(Calendar.SECOND, 00);
        firstDay.set(Calendar.MILLISECOND, 00);
        Log.d(TAG, "the Date " + date + "timeinmillies  first day " + firstDay.getTimeInMillis());


        // find last day of current open month
        Calendar lastDay = Calendar.getInstance();
        lastDay.setTime(date);

        lastDay.add(Calendar.MONTH, 1);
        lastDay.set(Calendar.DAY_OF_MONTH, 1);
        lastDay.add(Calendar.DATE, -1);
        lastDay.set(Calendar.HOUR_OF_DAY, 23);
        lastDay.set(Calendar.MINUTE, 59);
        lastDay.set(Calendar.SECOND, 00);
        lastDay.set(Calendar.MILLISECOND, 00);
        Log.d(TAG, "the Date " + date + "Timeinmillies  last day " + lastDay.getTimeInMillis());

        List<Event> user2isCurrentUser = FitForLifeDataManager.getInstance().User2isCurrentUser(firstDay.getTimeInMillis(), lastDay.getTimeInMillis(), currentUser.getEmail());
        Date today = new Date();
        for (Event tmp : user2isCurrentUser) {
            if (tmp.getTimeInMillis() - today.getTime() < 0) {
                tmp = new Event(Color.RED, tmp.getTimeInMillis(), tmp.getData());
                compactCalendar.addEvent(tmp);
            } else
                compactCalendar.addEvent(tmp);


//            Log.d(TAG, "User2isCurrentUser: " + user2isCurrentUser);
//        compactCalendar.addEvents(user2isCurrentUser);

        }
    }

    /**
     * only for count in adapter
     *
     * @param date
     * @return
     */
    public int CountMissingSessionThisMonthAdapter(Date date) {

        // find first day of current open month
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(date);

//        firstDay.add(Calendar.MONTH, 1);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
//        firstDay.add(Calendar.DATE, -1);
        firstDay.set(Calendar.HOUR_OF_DAY, 00);
        firstDay.set(Calendar.MINUTE, 01);
        firstDay.set(Calendar.SECOND, 00);
        firstDay.set(Calendar.MILLISECOND, 00);
        Log.d(TAG, "the Date " + date + "timeinmillies  first day " + firstDay.getTimeInMillis());


        // find last day of current open month
        Calendar lastDay = Calendar.getInstance();
        lastDay.setTime(date);

        lastDay.add(Calendar.MONTH, 1);
        lastDay.set(Calendar.DAY_OF_MONTH, 1);
        lastDay.add(Calendar.DATE, -1);
        lastDay.set(Calendar.HOUR_OF_DAY, 23);
        lastDay.set(Calendar.MINUTE, 59);
        lastDay.set(Calendar.SECOND, 00);
        lastDay.set(Calendar.MILLISECOND, 00);
        Log.d(TAG, "the Date " + date + "Timeinmillies  last day " + lastDay.getTimeInMillis());


        int num = userMissingSessionThisMonthAdapter(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());

        return num;
    }

    /**
     * to call from adapter all avaialble events
     *
     * @param date
     */

    public void addAvaialbeEventsForAdapter(Date date) {

        // find first day of current open month
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(date);

//        firstDay.add(Calendar.MONTH, 1);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
//        firstDay.add(Calendar.DATE, -1);
        firstDay.set(Calendar.HOUR_OF_DAY, 00);
        firstDay.set(Calendar.MINUTE, 01);
        firstDay.set(Calendar.SECOND, 00);
        firstDay.set(Calendar.MILLISECOND, 00);
        Log.d(TAG, "the Date " + date + "timeinmillies  first day " + firstDay.getTimeInMillis());


        // find last day of current open month
        Calendar lastDay = Calendar.getInstance();
        lastDay.setTime(date);

        lastDay.add(Calendar.MONTH, 1);
        lastDay.set(Calendar.DAY_OF_MONTH, 1);
        lastDay.add(Calendar.DATE, -1);
        lastDay.set(Calendar.HOUR_OF_DAY, 23);
        lastDay.set(Calendar.MINUTE, 59);
        lastDay.set(Calendar.SECOND, 00);
        lastDay.set(Calendar.MILLISECOND, 00);
        Log.d(TAG, "the Date " + date + "Timeinmillies  last day " + lastDay.getTimeInMillis());
        userMissingSessionThisMonth(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }

    /**
     * only for count
     *
     * @param firstDay
     * @param lastDay
     * @return
     */
    public int userMissingSessionThisMonthAdapter(long firstDay, long lastDay) {
        int count1 = FitForLifeDataManager.getInstance().getNotGoingCountThisMonth(firstDay, lastDay, currentUser.getEmail());
        int count2 = FitForLifeDataManager.getInstance().User2isCurrentUser(firstDay, lastDay, currentUser.getEmail()).size();
        Log.d(TAG, "not going this month" + count1);
        Log.d(TAG, "user 2 is nfsu " + count2);
        int numOfMissing = count1 - count2;
        return numOfMissing;
    }


    private void setNav() {
        if(isCoach) bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calendar:
                        Intent calendarIntent = new Intent(CalendarActivity.this, CalendarActivity.class);
                        startActivity(calendarIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.progress:
                        if (isCoach) {
                            Intent Progress = new Intent(CalendarActivity.this, TraineeProgressActivity.class);
                            startActivity(Progress);
                            overridePendingTransition(0, 0);
                            return true;
                        } else {
                            Intent ProgressIntent = new Intent(CalendarActivity.this, ProgressActivity.class);
                            startActivity(ProgressIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                    case R.id.home:
                        Intent homeIntent = new Intent(CalendarActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.payments:
                        Intent paymentsIntent;
                        if (isCoach) {
                            paymentsIntent = new Intent(CalendarActivity.this, TraineePaymentsActivity.class);
                        } else {
                            paymentsIntent = new Intent(CalendarActivity.this, PaymentsActivity.class);
                        }
                        startActivity(paymentsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        if (isCoach) {
                            Intent groupsIntent = new Intent(CalendarActivity.this, GroupsTraineeActivity.class);
                            startActivity(groupsIntent);
                            overridePendingTransition(0, 0);
                            return true;
                        }
                      else {
                            Intent profileIntent = new Intent(CalendarActivity.this, ProfileActivity.class);
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

    /**
     * OnClickMore
     * @param view - imageview
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(CalendarActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }
    /**
     *  LOGOUT METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(CalendarActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }
}


