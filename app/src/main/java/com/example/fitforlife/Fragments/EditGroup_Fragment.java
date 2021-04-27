package com.example.fitforlife.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

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

import androidx.annotation.NonNull;

import com.example.fitforlife.Activities.GroupsTraineeActivity;
import com.example.fitforlife.Activities.ManagerCoachTraineeGroupsActivity;
import com.example.fitforlife.Adapters.sessionPopupAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.fitforlife.Adapters.sessionAdapter;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.R;
import com.example.fitforlife.Model.Session;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditGroup_Fragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "EditGroup_Fragment";

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private ListView edit_sessionList;
    private NumberPicker numberTraineePicker;
    private FloatingActionButton addSession;

    List<Session> sessions = new ArrayList<>();
    List<Session> finalSessions = new ArrayList<>();

    private sessionAdapter adapter;
    View btsv;
    BottomSheetDialog btm;
    Session currentItemSession;
    private EditText groupName;
    private Group currentGroup;
    Context context;
    private String[] stringDuration;
    String[] weekDaysEn;
    String[] weekDays;
    String groupCoach;
    List<CoachInfo> coachTable;
    boolean isManager;
    Spinner spin;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    public EditGroup_Fragment(Group currentGroup) {
        // Required empty public constructor
        this.currentGroup = currentGroup;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_edit_group, container, false);
        context = getActivity();

        stringDuration = getResources().getStringArray(R.array.numberPickerDuration);
        groupCoach = currentGroup.getCoachId();
        ArrayList<Session> tmp = new ArrayList<>();
        currentGroup.setGroupSessions(tmp);
        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        spin = (Spinner) rootView.findViewById(R.id.EditGroup_coachSpinner);

        if (FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentUser() == null && FitForLifeDataManager.getInstance().getCurrentManager() != null) {
            coachTable = FitForLifeDataManager.getInstance().getAllManagerCoaches(FitForLifeDataManager.getInstance().getCurrentManager()); // for spinner
            ArrayAdapter<CoachInfo> adapter = new ArrayAdapter<CoachInfo>(getContext(), R.layout.simple_spinner_item, coachTable);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(adapter);

            Log.d(TAG, "coachTable    >>>" + coachTable);
            Log.d(TAG, "group coach  >>>>  " + FitForLifeDataManager.getInstance().getCoach(groupCoach));
            Log.d(TAG, " coachTable.indexOf(groupCoach) >>> " + coachTable.indexOf(FitForLifeDataManager.getInstance().getCoach(groupCoach)));
            CoachInfo tm22p = FitForLifeDataManager.getInstance().getCoach(groupCoach);
            Log.d(TAG, " coach >>" + tm22p.getId() + tm22p.getEmail() + tm22p.getFullName() + tm22p.getAge()
                    + tm22p.getPhone() + tm22p.getPassword() + tm22p.getType() + tm22p.getStudio()
            );
            spin.setSelection(coachTable.indexOf(tm22p));
            spin.setOnItemSelectedListener(this);
        } else {
            TextView coachName = rootView.findViewById(R.id.EditGroup_CoachName);
            coachName.setVisibility(View.GONE);
            spin.setVisibility(View.GONE);
        }

        Button save = rootView.findViewById(R.id.save_EditGroup);
        Button cancel = rootView.findViewById(R.id.cancel_EditGroup);
        save.setOnClickListener(SaveEditOnClick);
        cancel.setOnClickListener(CancelEditOnClick);

        addSession = rootView.findViewById(R.id.edit_addSession);
        groupName = rootView.findViewById(R.id.editGroupName);

        edit_sessionList = rootView.findViewById(R.id.edit_listSessions);
        edit_sessionList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //set group values
        groupName.setText(currentGroup.getGroupName());
        numberTraineePicker = rootView.findViewById(R.id.editNumberTrainee);
        numberTraineePicker.setMaxValue(50);
        numberTraineePicker.setMinValue(1);
        numberTraineePicker.setValue((Integer.parseInt(currentGroup.getNumberOfTrainee())));

        if (FitForLifeDataManager.getInstance().getAllGroupSessions(currentGroup).isEmpty()) {
            fStore.collection("groups").document(currentGroup.getGroupId()).collection("sessions")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "tajrbee " + document.getId() + " => " + document.getData());
                                    Long hour = (Long) document.get("hour");
                                    int y1 = hour.intValue();
                                    Long min = ((Long) document.get("minute"));
                                    int y2 = min.intValue();
                                    Long duration = (Long) document.get("duration");
                                    int y3 = duration.intValue();
                                    Session newSession = new Session(currentGroup.getGroupId(), document.getId(), document.getData().get("day").toString(), y1
                                            , y2, y3);
                                    currentGroup.getGroupSessions().add(newSession);
                                    FitForLifeDataManager.getInstance().createSession(newSession);
                                    adapter = new sessionAdapter(context, R.layout.card_session, currentGroup.getGroupSessions());
                                    edit_sessionList.setAdapter(adapter);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            Log.d(TAG, "jab lsession mn sql " + FitForLifeDataManager.getInstance().getAllGroupSessions(currentGroup));
            adapter = new sessionAdapter(context, R.layout.card_session, FitForLifeDataManager.getInstance().getAllGroupSessions(currentGroup));
            edit_sessionList.setAdapter(adapter);
        }
        /*
        /* Fill Edit text with current group details
         */

//        if(FitForLifeDataManager.getInstance().getAllGroupSessions(currentGroup).isEmpty()){
//
//        CollectionReference docRef = fStore.collection("groups").document(currentGroup.getGroupId()).collection("sessions");
//        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
//
//                if (e != null) {
//                    return;
//                }
//
//                if (snapshot != null && !snapshot.isEmpty()) {
//
//                    ArrayList<Session> tmp = new ArrayList<>();
//                    tmp.clear();
//                    currentGroup.setGroupSessions(tmp);
//
//                    for (DocumentSnapshot document2 : snapshot.getDocuments()) {
//                        Long hour = (Long) document2.get("hour");
//                        int y1 = hour.intValue();
//                        Long min = ((Long) document2.get("minute"));
//                        int y2 = min.intValue();
//                        Long duration = (Long) document2.get("duration");
//                        int y3 = duration.intValue();
//
//                        Session newSession = new Session(currentGroup.getGroupId(),document2.getId(), document2.getData().get("day").toString(), y1
//                                , y2, y3);
//                      //  if(currentGroup.getGroupSessions()!=null) {
//                            currentGroup.getGroupSessions().add(newSession);
//                            FitForLifeDataManager.getInstance().createSession(newSession);
//                       // }
//
//
//                    }
//                    if(currentGroup.getGroupSessions()!=null ){
//                    Log.d(TAG, "group Sessions" + currentGroup.toString());
//                    adapter = new sessionAdapter(context, R.layout.card_session, currentGroup.getGroupSessions());
//                    edit_sessionList.setAdapter(adapter);}
//                } else {
//                    Log.d(TAG, "Current data: null e3ne fsh sessions bntaym");
//
//                }
//            }
//        });}
//        else{
//            Log.d(TAG, "group sessions msh fadee bl sql " + FitForLifeDataManager.getInstance().getAllGroupSessions(currentGroup));
//            adapter = new sessionAdapter(context, R.layout.card_session, FitForLifeDataManager.getInstance().getAllGroupSessions(currentGroup));
//            edit_sessionList.setAdapter(adapter);
//        }


        edit_sessionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


                sessionTitle = btsv.findViewById(R.id.sessionNumber);
                sessionTitle.setText(currentItemSession.getSessionNumber());

                sessionDayPicker = btsv.findViewById(R.id.sessionsDayPicker);
                sessionDayPicker.setMaxValue(6);
                sessionDayPicker.setMinValue(0);
                final String[] weekDays = getResources().getStringArray(R.array.weekDays);
                sessionDayPicker.setDisplayedValues(weekDays);
                for (int i = 0; i < weekDays.length; i++) {
                    if (weekDays[i].equals(currentItemSession.getDay()))
                        sessionDayPicker.setValue(i);
                }

                timePicker = (TimePicker) btsv.findViewById(R.id.sessionTimePicker);
                timePicker.setIs24HourView(true);
                setTimePickerInterval(timePicker);
                timePicker.setMinute(currentItemSession.getMinute());
                timePicker.setHour(currentItemSession.getHour());


                sessionDurationPicker = btsv.findViewById(R.id.sessionDurationPicker);
//                sessionDurationPicker.setMinValue(0);
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

                btm.show();

            }

            //to be continued
            private void setOnClick(TextView saveSession) {
                saveSession.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        NumberPicker day = btsv.findViewById(R.id.sessionsDayPicker);
                        NumberPicker duration = btsv.findViewById(R.id.sessionDurationPicker);
                        TimePicker time = btsv.findViewById(R.id.sessionTimePicker);

                        currentItemSession.setDay(day.getDisplayedValues()[day.getValue()]);
                        currentItemSession.setDuration(Integer.valueOf(duration.getDisplayedValues()[duration.getValue()]));
                        currentItemSession.setHour(time.getHour());
                        currentItemSession.setMinute(time.getMinute());
                        adapter.notifyDataSetChanged();
                        btm.dismiss();

                    }
                });
            }
        });

        //saveeeeee
        // Add session listener
        addSession.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int maxSessionPerWeek = 4;
                if (edit_sessionList.getCount() >= maxSessionPerWeek) {
                    Toast.makeText(getActivity(), "max sessions per week is " + maxSessionPerWeek, Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Session> mySessionsUpdated = new ArrayList<>();
                    View v2;
                    TextView day;
                    TextView hour;
                    TextView minute;
                    TextView number;
                    TextView duration;

                    for (int i = 0; i < edit_sessionList.getCount(); i++) {

                        v2 = edit_sessionList.getChildAt(i);
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
                    edit_sessionList.setAdapter(adapter);

                }
            }
        });


        return rootView;
    }


    private View.OnClickListener CancelEditOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (isManager)
                ManagerCoachTraineeGroupsActivity.SetFragment(false, false, true);
            else
                GroupsTraineeActivity.SetFragment(false);
            getActivity().onBackPressed();


        }
    };


    private View.OnClickListener SaveEditOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (!checkError()) {
                String groupname = groupName.getText().toString();
                String traineeNumber = "" + numberTraineePicker.getValue();
                Map<String, Object> groups = new HashMap<String, Object>();
                groups.put("groupName", groupname);
                groups.put("NumberOfTrainee", traineeNumber);
                if (spin.isShown()) {
                    currentGroup.setCoachId(((CoachInfo) spin.getSelectedItem()).getEmail());
                }
                groups.put("coachId", groupCoach);
                finalSessions = getAllSessions();
                if (finalSessions != null) {
                    final Group updateGroup = new Group(currentGroup.getGroupId(), groupname, traineeNumber, currentGroup.getCoachId());

                    final DocumentReference documentReference = fStore.collection("groups").document(currentGroup.getGroupId());
                    documentReference.set(groups).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FitForLifeDataManager.getInstance().deleteGroupSessions(currentGroup);
                            FitForLifeDataManager.getInstance().updateGroup(updateGroup);

                            Log.d(TAG, "final sessions" + finalSessions);

                            for (final Session tmp : finalSessions) {
                                tmp.setGroupId(documentReference.getId());
                                fStore.collection("groups").document(documentReference.getId()).collection("sessions").
                                        document(tmp.getSessionNumber()).set(tmp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "added  sessions to groups : " + tmp.toString());
                                        //should delete all sessions to this group , and add again new sessions
                                        //need to implement get all session by group id , then add session
                                        FitForLifeDataManager.getInstance().createSession(tmp);
                                        Log.d(TAG, "b3ed  createSession()" + FitForLifeDataManager.getInstance().getAllGroupSessions(currentGroup));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });
                    if (isManager)
                        ManagerCoachTraineeGroupsActivity.SetFragment(false, false, true);
                    else
                        GroupsTraineeActivity.SetFragment(false);
                    getActivity().onBackPressed();
                }
            }
        }

    };


    // getting data from listview on save click --- needs to be updated

    public ArrayList<Session> getAllSessions() {
        View v2;
        TextView day;
        TextView hour;
        TextView minute;
        TextView number;
        TextView duration;
        String daytxt = "";

        ArrayList<Session> mySessions = new ArrayList<Session>();

        for (int i = 0; i < edit_sessionList.getCount(); i++) {

            v2 = edit_sessionList.getChildAt(i);
            number = v2.findViewById(R.id.session_number);
            day = v2.findViewById(R.id.session_day);
            hour = v2.findViewById(R.id.session_hour);
            minute = v2.findViewById(R.id.session_minute);
            duration = v2.findViewById(R.id.session_duration);

            weekDaysEn = context.getResources().getStringArray(R.array.weekDaysEn);
            for (String d : weekDaysEn) {
                if (day.getText().equals(d))
                    daytxt = d;
            }

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

    private int getDurationIndex(int duration) {
        int j = 0;
        for (int i = 0; i < stringDuration.length; i++) {
            if (stringDuration[i].equals(String.valueOf(duration)))
                return j = i;

        }
        return -1;
    }

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
        groupCoach = coachTable.get(position).getEmail();


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