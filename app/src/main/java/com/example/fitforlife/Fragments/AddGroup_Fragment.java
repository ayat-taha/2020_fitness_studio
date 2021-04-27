package com.example.fitforlife.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fitforlife.Activities.GroupsTraineeActivity;
import com.example.fitforlife.Activities.ManagerCoachTraineeGroupsActivity;
import com.example.fitforlife.Adapters.TraineePopUpAdapter;
import com.example.fitforlife.Adapters.sessionAdapter;
import com.example.fitforlife.Adapters.sessionPopupAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGroup_Fragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "AddGroup_Fragment";

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private ListView sessionList;
    private NumberPicker numberTraineePicker;
    private FloatingActionButton addSession;

    List<Session> sessions = new ArrayList<>();
    List<Session> finalSessions = new ArrayList<>();
    private sessionAdapter adapter;
    View btsv;
    BottomSheetDialog btm;
    Session currentItemSession;
    private EditText groupName;
    private boolean isManager;
    String currentUser;
    Group newGroup;
    List<CoachInfo> coachTable;
    private String[] stringDuration;
    String[] weekDaysEn;
    String[] weekDays;
    Context context;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    public AddGroup_Fragment(boolean isManager) {
        // Required empty public constructor
        this.isManager = isManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_add_group, container, false);
        context = getActivity();
        stringDuration = getResources().getStringArray(R.array.numberPickerDuration);

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize_auth]
        Spinner spin = (Spinner) rootView.findViewById(R.id.coachSpinner);


        if (isManager) {
            coachTable = FitForLifeDataManager.getInstance().getAllManagerCoaches(FitForLifeDataManager.getInstance().getCurrentManager()); // for spinner
            ArrayAdapter<CoachInfo> adapter = new ArrayAdapter<CoachInfo>(getContext(), R.layout.simple_spinner_item, coachTable);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(adapter);
            spin.setSelection(0); //set selection to all groups
            spin.setOnItemSelectedListener(this);

        } else {
            TextView coachName = rootView.findViewById(R.id.CoachName);
            coachName.setVisibility(View.GONE);
            spin.setVisibility(View.GONE);
            currentUser = fAuth.getCurrentUser().getEmail();
        }
        Button save = rootView.findViewById(R.id.save_addGroup);
        save.setOnClickListener(SaveAddOnClick);

        Button cancel = rootView.findViewById(R.id.cancel_addGroup);
        cancel.setOnClickListener(CancelEditOnClick);

        addSession = rootView.findViewById(R.id.addSession);
        sessionList = rootView.findViewById(R.id.listSessions);
        sessionList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        sessions.add(new Session(" " + sessions.size() + 1));
        adapter = new sessionAdapter(context, R.layout.card_session, sessions);
        sessionList.setAdapter(adapter);

        groupName = rootView.findViewById(R.id.addFullnameGroup);
        numberTraineePicker = rootView.findViewById(R.id.addNumberTrainee);
        numberTraineePicker.setMaxValue(50);
        numberTraineePicker.setMinValue(1);

        // update session details bottom sheet dialog

        sessionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                currentItemSession = adapter.getItem(position);
                NumberPicker sessionDayPicker;
                NumberPicker sessionDurationPicker;
                TimePicker timePicker;
                TextView sessionTitle;
                TextView saveSession;


                btm = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                btsv = LayoutInflater.from(getActivity().getApplicationContext())
                        .inflate(R.layout.card_add_sessions, (RelativeLayout) rootView.findViewById(R.id.sessionSheetContainer));
                btm.setContentView(btsv);
                btm.show();


                sessionTitle = btsv.findViewById(R.id.sessionNumber);
                sessionTitle.setText(currentItemSession.getSessionNumber());

                sessionDayPicker = btsv.findViewById(R.id.sessionsDayPicker);
                sessionDayPicker.setMaxValue(6);
                sessionDayPicker.setMinValue(0);
                weekDays = getResources().getStringArray(R.array.weekDays);
                weekDaysEn = getResources().getStringArray(R.array.weekDaysEn);
                sessionDayPicker.setDisplayedValues(weekDays);
                for (int i = 0; i < weekDays.length; i++) {
                    if (weekDaysEn[i].equals(currentItemSession.getDay()))
                        sessionDayPicker.setValue(i);
                }

                timePicker = (TimePicker) btsv.findViewById(R.id.sessionTimePicker);
                timePicker.setIs24HourView(true);
                setTimePickerInterval(timePicker);

                timePicker.setMinute(currentItemSession.getMinute());
                timePicker.setHour(currentItemSession.getHour());

                sessionDurationPicker = btsv.findViewById(R.id.sessionDurationPicker);
//                               sessionDurationPicker.setMinValue(0);
//                sessionDurationPicker.setMaxValue(60);
//                sessionDurationPicker.setValue(currentItemSession.getDuration());

                sessionDurationPicker.setMinValue(0);
                sessionDurationPicker.setMaxValue(7);
                sessionDurationPicker.setDisplayedValues(stringDuration);
                int i = getDurationIndex(currentItemSession.getDuration());
                if (i > -1)
                    sessionDurationPicker.setValue(i);

                saveSession = btsv.findViewById(R.id.save_session);
                setOnClick(saveSession);
            }

            private void setOnClick(TextView saveSession) {
                saveSession.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        NumberPicker day = btsv.findViewById(R.id.sessionsDayPicker);
                        NumberPicker duration = btsv.findViewById(R.id.sessionDurationPicker);
                        TimePicker time = btsv.findViewById(R.id.sessionTimePicker);

                        currentItemSession.setDay(weekDaysEn[day.getValue()]);
                        currentItemSession.setDuration(Integer.valueOf(duration.getDisplayedValues()[duration.getValue()]));
                        currentItemSession.setHour(time.getHour());
                        currentItemSession.setMinute(time.getMinute() * 5);
                        adapter.notifyDataSetChanged();
                        btm.dismiss();

                    }
                });
            }
        });


        // Add session listener
        addSession.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int maxSessionPerWeek = 4;
                if (sessionList.getCount() >= maxSessionPerWeek) {
                    Toast.makeText(getActivity(), "max sessions per week is " + maxSessionPerWeek, Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Session> mySessionsUpdated = new ArrayList<>();
                    View v2;
                    TextView day;
                    TextView hour;
                    TextView minute;
                    TextView number;
                    TextView duration;

                    for (int i = 0; i < sessionList.getCount(); i++) {

                        v2 = sessionList.getChildAt(i);
                        number = v2.findViewById(R.id.session_number);
                        day = v2.findViewById(R.id.session_day);
                        hour = v2.findViewById(R.id.session_hour);
                        minute = v2.findViewById(R.id.session_minute);
                        duration = v2.findViewById(R.id.session_duration);
                        mySessionsUpdated.add(new Session(number.getText().toString(),
                                day.getText().toString(), Integer.parseInt(hour.getText().toString()),
                                Integer.parseInt(minute.getText().toString()), Integer.parseInt(duration.getText().toString())));

                    }
                    int count = mySessionsUpdated.size() + 1;
                    mySessionsUpdated.add(new Session(" " + count));
                    Log.d(" mySessions", "my sessions data " + mySessionsUpdated.toString());
                    adapter = new sessionAdapter(getActivity(), R.layout.card_add_sessions, mySessionsUpdated);
                    sessionList.setAdapter(adapter);

                }
            }
        });


        return rootView;
    }

    private int getDurationIndex(int duration) {
        int j = 0;
        for (int i = 0; i < stringDuration.length; i++) {
            if (stringDuration[i].equals(String.valueOf(duration)))
                return j = i;

        }
        return -1;
    }


    // getting data from list view on save click --- needs to be updated
    public ArrayList<Session> getAllSessions() {
        View v2;
        TextView day;
        TextView hour;
        TextView minute;
        TextView number;
        TextView duration;

        ArrayList<Session> mySessions = new ArrayList<Session>();

        for (int i = 0; i < sessionList.getCount(); i++) {

            v2 = sessionList.getChildAt(i);
            number = v2.findViewById(R.id.session_number);
            day = v2.findViewById(R.id.session_day);
            hour = v2.findViewById(R.id.session_hour);
            minute = v2.findViewById(R.id.session_minute);
            duration = v2.findViewById(R.id.session_duration);
            Session newSession = new Session(number.getText().toString(),
                    day.getText().toString(), Integer.parseInt(hour.getText().toString()),
                    Integer.parseInt(minute.getText().toString()), Integer.parseInt(duration.getText().toString()));
            List<Session> overlappedSessions = FitForLifeDataManager.getInstance().sessionsOverlap(newSession);
            if (overlappedSessions.isEmpty()) {
                mySessions.add(newSession);
            } else {
                dialogBuilder = new AlertDialog.Builder(context);
                final View sessionPopUpView = ((Activity) context).getLayoutInflater().inflate(R.layout.error_add_session_popup, null);
                dialogBuilder.setView(sessionPopUpView);
                dialog = dialogBuilder.create();

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
                        dialog.dismiss();
                    }
                });
                dialogBuilder.setTitle(getResources().getString(R.string.addSessionFailed));
                dialog.setTitle(getResources().getString(R.string.addSessionFailed));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //to round popup corners
                dialog.show();
                return null;
            }
        }
        return mySessions;
    }

    private View.OnClickListener CancelEditOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (isManager) {
                ManagerCoachTraineeGroupsActivity.SetFragment(false, false, true);
            } else
                GroupsTraineeActivity.SetFragment(false);

            getActivity().onBackPressed();
        }
    };


    // getting data from listview on save click --- needs to be updated
    private View.OnClickListener SaveAddOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (!checkError()) {
                final String groupname = groupName.getText().toString();
                final String traineeNumber = "" + numberTraineePicker.getValue();
                Map<String, Object> groups = new HashMap<String, Object>();
                groups.put("groupName", groupname);
                groups.put("NumberOfTrainee", traineeNumber);
                groups.put("coachId", currentUser);
                finalSessions = getAllSessions();

                if (finalSessions != null) {
//                 newGroup=new Group(groupname,traineeNumber,groupCoach,sessions);
                    fStore.collection("groups").add(groups).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                                newGroup.setGroupId(documentReference.getId());
                            Log.d(TAG, "Session list : " + finalSessions.toString());
                            Group newGroup = new Group(documentReference.getId(), groupname, traineeNumber, currentUser);
                            Log.d(TAG, "new group " + newGroup.toString());
                            FitForLifeDataManager.getInstance().createGroup(newGroup);

                            for (final Session tmp : finalSessions) {
                                tmp.setGroupId(documentReference.getId());
                                fStore.collection("groups").document(documentReference.getId()).collection("sessions").
                                        document(tmp.getSessionNumber()).set(tmp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FitForLifeDataManager.getInstance().createSession(tmp);
                                        Log.d(TAG, "added  sessions to groups : " + tmp.toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                            if (isManager)
                                ManagerCoachTraineeGroupsActivity.SetFragment(false, false, true);
                            else
                                GroupsTraineeActivity.SetFragment(false);
                            getActivity().onBackPressed();

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }
            }
        }
    };

    private Boolean checkError() {
        String groupname = groupName.getText().toString();
        boolean checkError = true;

        while (checkError) {

            boolean feError = false;

            if (TextUtils.isEmpty(groupname)) {
                groupName.setError("Group Name Is required");
                feError = true;
            }

            if (feError)
                return true;
            else
                checkError = false;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentUser = coachTable.get(position).getEmail();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
