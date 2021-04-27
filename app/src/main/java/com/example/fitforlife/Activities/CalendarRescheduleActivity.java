package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fitforlife.Adapters.EventArrayAdapter;
import com.example.fitforlife.Adapters.sessionPopupAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarRescheduleActivity extends AppCompatActivity {
    private static final String TAG = "RescheduleCalendar";
    FirebaseFirestore fStore;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TimePicker sessionTime;
    private NumberPicker sessionDuration;
    private Button saveNewSess, cancelNewSess;
    private ListView eventList;
    private EventArrayAdapter adapter;
    boolean isCoach;
    TextView monthYear, canceledSession;
    Date today;
    Date DateClicked;
    private Group currentGroup;
    private List<Session> sessionList;
    List<Event> canceledEventsList;
    List<Event> resEvents;
    Context context;
    private CoachInfo currentCoach;
    private android.app.AlertDialog.Builder dialogBuilderSession;
    private android.app.AlertDialog dialogSession;

    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_reschedule);
         context = CalendarRescheduleActivity.this;
        fStore = FirebaseFirestore.getInstance();

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));


        //list
        eventList = findViewById(R.id.AllEventList);

        // set title for calendar
        final Date date = new Date();

        //fill canceled event details in title
        canceledSession = findViewById(R.id.dateForCanceledSession);

        Event eventDetails = FitForLifeDataManager.getInstance().getCurrentCanceledEvent();
        Session eventData = ((Session) eventDetails.getData());
        Date currentDate = new Date(eventDetails.getTimeInMillis());
        DateFormat df = new SimpleDateFormat("dd/M/yyyy hh:mm");
        Group group = FitForLifeDataManager.getInstance().getGroup((eventData.getGroupId()));
        canceledSession.setText("" + (df.format(currentDate) + " Group :" + group.getGroupName()));

        // calendar title
        monthYear = findViewById(R.id.resMonthYear);
        monthYear.setText(dateFormatMonth.format(date));
        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendarRec_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);


        onClickCal();
        canceledEventsList = new ArrayList<>();
        sessionList = new ArrayList<>();
        resEvents = new ArrayList<>();
        //current is **** Coach ****
        if (FitForLifeDataManager.getInstance().getCurrentCoach() != null && FitForLifeDataManager.getInstance().getCurrentManager() == null) {
            currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
            List<Group> coachGroups = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
            for (Group tmpGroup : coachGroups) {
                canceledEventsList.addAll(FitForLifeDataManager.getInstance().getAllCanceledByGroupId(tmpGroup.getGroupId()));
                sessionList.addAll(FitForLifeDataManager.getInstance().getAllGroupSessions(tmpGroup));
                resEvents.addAll(FitForLifeDataManager.getInstance().getAllGroupRescheduleSessions(tmpGroup));
            }

        }

        if (FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentManager() != null) {
            currentCoach = FitForLifeDataManager.getInstance().getCurrentManager();
            sessionList = FitForLifeDataManager.getInstance().getAllManagerSessions(currentCoach);
            canceledEventsList = FitForLifeDataManager.getInstance().getAllManagerCanceled(currentCoach);
            resEvents = FitForLifeDataManager.getInstance().getAllManagerReschedule(currentCoach);
        }

        final Date date2 = new Date();
        findDays(date2);

    }


    public void onClickCal() {
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = CalendarRescheduleActivity.this;
                DateClicked = dateClicked;
                adapter = new EventArrayAdapter(context, R.layout.card_events, compactCalendar.getEvents(dateClicked), isCoach, true);
                eventList.setAdapter(adapter);
                Log.d(TAG, "dateClicked  " + DateClicked.getDay());


                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date today = new Date();
                Date todayWithZeroTime = new Date();
                Date clickedFormatted = dateClicked;
                try {
                    todayWithZeroTime = formatter.parse(formatter.format(today));
                    clickedFormatted = formatter.parse(formatter.format(dateClicked));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

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
     * get All sessions
     */
    public void findDays(Date month) {

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


//                if (cal.getTimeInMillis() - today.getTime() < 0) {
                ev1 = new Event(Color.RED, cal.getTimeInMillis(), tmpSession);
//                } else {
//                    ev1 = new Event(Color.GREEN, cal.getTimeInMillis(), tmpSession);
//                }

//                if (!canceledEventsList.isEmpty()) {
//                    if(!checkIfCanceled(ev1)){
//                        compactCalendar.addEvent(ev1);
//
//                    }
//                } else
                compactCalendar.addEvent(ev1);

//            }
                cal.add(Calendar.DAY_OF_MONTH, 7);

            }

        }
        checkIfExist(month);
        findRes(month);

    }


    private void findRes(Date date) {
        List<Event> calendarEvents = compactCalendar.getEvents(date);
        for (Event tmp : resEvents) {
            if (!calendarEvents.contains(tmp)) {
                Log.d(TAG, "findRes :  " + tmp);
                compactCalendar.addEvent(tmp);

            }
        }
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


    public boolean checkIfExist(Date month) {
        for (Event event : compactCalendar.getEventsForMonth(month)) {
            for (Event tmp : canceledEventsList) {
                if (tmp.getTimeInMillis() == event.getTimeInMillis() && ((Session) tmp.getData()).getGroupId().equals(((Session) event.getData()).getGroupId()))
                    compactCalendar.removeEvent(event);
            }
        }
        return false;
    }


    public void addOnClick(View view) {
        if (DateClicked == null) {
            Toast.makeText(CalendarRescheduleActivity.this, getResources().getString(R.string.selectDay), Toast.LENGTH_LONG).show();
            return;
        }

        if (DateClicked.before(new Date())) {
            Toast.makeText(CalendarRescheduleActivity.this, getResources().getString(R.string.selectValidDay), Toast.LENGTH_LONG).show();
            return;
        }

        dialogBuilder = new AlertDialog.Builder(this);
        final View sessionPopUpView = getLayoutInflater().inflate(R.layout.sessionpopup, null);
        sessionTime = sessionPopUpView.findViewById(R.id.sessionTimePickerRes);
        sessionTime.setIs24HourView(true);
        setTimePickerInterval(sessionTime);

        sessionDuration = sessionPopUpView.findViewById(R.id.sessionDurationPickerRes);
        sessionDuration.setMinValue(0);
        sessionDuration.setMaxValue(7);
        final String[] stringDuration = getResources().getStringArray(R.array.numberPickerDuration);
        sessionDuration.setDisplayedValues(stringDuration);



        sessionPopUpView.setClipToOutline(true);
        saveNewSess = sessionPopUpView.findViewById(R.id.addSessionRes);
        cancelNewSess = sessionPopUpView.findViewById(R.id.cancelSessionRes);

        dialogBuilder.setView(sessionPopUpView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //to round popup corners

        dialog.show();


        saveNewSess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Event currentCanceled = FitForLifeDataManager.getInstance().getCurrentCanceledEvent();
                final String canceledGroupId = ((Session) currentCanceled.getData()).getGroupId();
                final String canceledFstoreId = FitForLifeDataManager.getInstance().getCanceledId(currentCanceled);
                List<Session> groupSessions = FitForLifeDataManager.getInstance().getAllGroupSessions(new Group(canceledGroupId));
                int sessionCount = groupSessions.size();
                String durationTamp = stringDuration[sessionDuration.getValue()];
                final Session newSession = new Session(canceledGroupId, Integer.toString(sessionCount + 1), findweekDay2(DateClicked.getDay()), sessionTime.getHour(), sessionTime.getMinute(), Integer.valueOf(durationTamp));

                List<Session> overlappedSessions = FitForLifeDataManager.getInstance().sessionsOverlap(newSession);
                if (overlappedSessions.isEmpty()) {
                    int i = findweekDay(newSession.getDay());
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    cal.setTime(DateClicked);
                    cal.set(Calendar.HOUR_OF_DAY, newSession.getHour());
                    cal.set(Calendar.MINUTE, newSession.getMinute());
                    cal.set(Calendar.SECOND, 00);
                    cal.set(Calendar.MONTH, DateClicked.getMonth());
                    cal.set(Calendar.MILLISECOND, 00);

                    final Event resEvent = new Event(Color.BLACK, cal.getTimeInMillis(), newSession);
                    Log.d(TAG, "cal get time in millies : " + cal.getTimeInMillis());
                    Map<String, Object> data = new HashMap<>();
                    data.put("color", resEvent.getColor());
                    data.put("timeInMillis", resEvent.getTimeInMillis());
                    data.put("sessionNumber", ((Session) resEvent.getData()).getSessionNumber());
                    data.put("day", ((Session) resEvent.getData()).getDay());
                    data.put("hour", ((Session) resEvent.getData()).getHour());
                    data.put("minute", ((Session) resEvent.getData()).getMinute());
                    data.put("duration", ((Session) resEvent.getData()).getDuration());
                    data.put("groupId", ((Session) resEvent.getData()).getGroupId());

                    fStore.collection("rescheduledSessions").document(canceledFstoreId)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    resEvents.add(resEvent);
                                    compactCalendar.addEvent(resEvent);
                                    adapter.notifyDataSetChanged();

                                    FloatingActionButton add = findViewById(R.id.addSession);
                                    TextView text = findViewById(R.id.text);
                                    text.setText(getResources().getString(R.string.resCanceledSession));
                                    add.hide();
                                    FitForLifeDataManager.getInstance().createRescheduledSession(resEvent, canceledFstoreId);
                                    addNotification(canceledGroupId, currentCoach, currentCanceled);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                    dialog.dismiss();
                } else {
                    dialogBuilderSession = new android.app.AlertDialog.Builder(context);
                    final View sessionPopUpView = ((Activity) context).getLayoutInflater().inflate(R.layout.error_add_session_popup, null);
                    dialogBuilderSession.setView(sessionPopUpView);
                    dialogSession = dialogBuilderSession.create();

                    TextView sessionNumber, sessionDay, sessionDuration, sessionHour, sessionMinute;

                    final String[] weekDays = context.getResources().getStringArray(R.array.weekDays);
                    final String[] weekDaysEn = context.getResources().getStringArray(R.array.weekDaysEn);


                    sessionDay = sessionPopUpView.findViewById(R.id.session_day);
                    int dayIndex = 0;
                    for (int j = 0; j < weekDays.length; j++) {
                        if (weekDaysEn[j].equals(newSession.getDay()))
                            dayIndex = j;
                    }
                    sessionDay.setText(weekDays[dayIndex]);

                    sessionDuration = sessionPopUpView.findViewById(R.id.session_duration);
                    sessionDuration.setText(String.valueOf(newSession.getDuration()));

                    sessionNumber = sessionPopUpView.findViewById(R.id.session_number);
                    sessionNumber.setText(String.valueOf(newSession.getSessionNumber()));

                    sessionHour = sessionPopUpView.findViewById(R.id.session_hour);
                    sessionMinute = sessionPopUpView.findViewById(R.id.session_minute);
                    sessionHour.setText("" + newSession.getHour());
                    sessionMinute.setText("" + newSession.getMinute());

                    ListView list = sessionPopUpView.findViewById(R.id.sessionList);
                    List<Session> sessions = overlappedSessions;

                    sessionPopupAdapter sessionAdapter = new sessionPopupAdapter(context, R.layout.card_trainee_popup, sessions);
                    list.setAdapter(sessionAdapter);
                    TextView close = sessionPopUpView.findViewById(R.id.txtclose);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogSession.dismiss();
                        }
                    });
                    dialogBuilderSession.setTitle(getResources().getString(R.string.addSessionFailed));
                    dialogSession.setTitle(getResources().getString(R.string.addSessionFailed));
                    dialogSession.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //to round popup corners
                    dialogSession.show();
                }
            }
        });

        cancelNewSess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private String findweekDay2(int day) {
        String i = null;
        switch (day) {
            case 0:
                i = "Sunday";
                break;
            case 1:
                i = "Monday";
                break;
            case 2:
                i = "Tuesday";
                break;
            case 3:
                i = "Wednesday";
                break;
            case 4:
                i = "Thursday";
                break;
            case 5:
                i = "Friday";
                break;
            case 6:
                i = "Saturday";
                break;
        }
        return i;
    }

    /**
     * OnClickMore METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(CalendarRescheduleActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(CalendarRescheduleActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }

    private void addNotification(String groupid, CoachInfo currentCoach, Event event) {
        List<UserInfo> groupTrainee = FitForLifeDataManager.getInstance().getAllGroupTrainee(new Group(groupid));
        for (UserInfo trainee : groupTrainee)
            addUserNotification(trainee.getId(), currentCoach, event);
    }

    private void addUserNotification(String userid, CoachInfo currentCoach, Event event) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy ");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", currentCoach.getId());
        hashMap.put("text", "Rescheduled canceled session for");
        hashMap.put("text2", "");
        hashMap.put("postid", (String) formatter.format(event.getTimeInMillis()));
        hashMap.put("isEventRes", true);
        hashMap.put("isEventCanc", false);
        hashMap.put("isEventNotGoing", false);
        hashMap.put("isEventGoing", false);
        hashMap.put("isProgress", false);
        hashMap.put("ispost", false);
        reference.push().setValue(hashMap);
    }

    @SuppressLint("NewApi")
    private void setTimePickerInterval(TimePicker timePicker) {
        int TIME_PICKER_INTERVAL = 5;
        NumberPicker minutePicker;
        List<String> displayedValues;
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");

            Field field = classForid.getField("minute");
            minutePicker = (NumberPicker) timePicker
                    .findViewById(field.getInt(null));

            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(11);
            displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }

            minutePicker.setDisplayedValues(displayedValues
                    .toArray(new String[0]));
            minutePicker.setWrapSelectorWheel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}