package com.example.fitforlife.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fitforlife.Activities.CalendarActivity;
import com.example.fitforlife.Fragments.Attendance_Fragment;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventArrayAdapter extends ArrayAdapter<Event> {

    private static final String TAG = "EventArrayAdapter";

    private List<Event> EventList;
    private Context context;
    private Activity ctx;
    private Button button;
    private boolean isCoach;
    private boolean isRes;
    private Event currentEventTest;

    private CoachInfo currentCoach;
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    public EventArrayAdapter(Context context, int resource, List<Event> data, boolean isCoach, boolean isRes) {
        super(context, resource, data);
        this.EventList = data;
        this.context = context;
        this.isCoach = isCoach;
        if (isCoach) {
            if (FitForLifeDataManager.getInstance().getCurrentCoach() != null)
                currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
        }
        this.isRes = isRes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize_auth]


        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.card_events, null, false);
        currentEventTest = getItem(position);
        final Event currentEvent = getItem(position);
        TextView vDate = v.findViewById(R.id.date_event);
        TextView vTime = v.findViewById(R.id.time_event);
        TextView vduration = v.findViewById(R.id.duration_event);
        TextView vgroup = v.findViewById(R.id.group_event);
        button = v.findViewById(R.id.not_going);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy ");
        DateFormat formatter2 = new SimpleDateFormat("HH:mm  ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentEvent.getTimeInMillis());
        vDate.setText(formatter.format(calendar.getTime()));
        vTime.setText(formatter2.format(calendar.getTime()));
        vduration.setText("" + ((Session) (currentEvent.getData())).getDuration());

        Group groupName = FitForLifeDataManager.getInstance().getGroup(((Session) (currentEvent.getData())).getGroupId());
        vgroup.setText(groupName.getGroupName());
        if (isRes) {
            button.setVisibility(View.GONE);

        }
        //Buttons For ***** Trainee *******
        if (!isCoach) {
            if (currentEvent.getColor() == Color.RED) {
                button.setVisibility(View.GONE);
            } else if (currentEvent.getColor() == Color.YELLOW) {
                button.setText(context.getResources().getText(R.string.going));
                GoingOnClick(currentEvent);
            } else if (currentEvent.getColor() == Color.GREEN) {
                button.setText(context.getResources().getText(R.string.not_going));
                notGoingOnClick(currentEvent);
            }
        } // END



        //Buttons For *****coach *******
        if (isCoach) {
            Date today = new Date();
            long hourInMillis = 3600000;

            if (currentEvent.getColor() == Color.RED) {
                button.setVisibility(View.GONE);
            }

            if (currentEvent.getColor() == Color.GREEN || currentEvent.getColor() == Color.BLACK) {
                if (currentEvent.getTimeInMillis() - today.getTime() > hourInMillis * 2) {
                    button.setText(context.getResources().getText(R.string.cancelSession));
                    cancelSession(currentEvent);
                }
//                else {
//                    button.setText(context.getResources().getText(R.string.takeAttendance));
//                    button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                            Attendance_Fragment attendence_fragment = new Attendance_Fragment();
//                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.calendarContent, attendence_fragment).addToBackStack(null).commit();
//                        }
//                    });
//                }
            }
        }  // END IS **** COACH*****


        return v;
    }

    private void GoingOnClick(final Event currentEvent) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Event tmp = new Event(Color.GREEN, currentEvent.getTimeInMillis(), currentEvent.getData());
                EventList.set(getPosition(currentEvent), tmp);
                EventArrayAdapter.this.notifyDataSetChanged();

                final String id = FitForLifeDataManager.getInstance().getNotGoingId(currentEvent);
                //update user2 in fire base

                DocumentReference eventRef = fStore.collection("notGoing").document(id);

                eventRef
                        .update("user2", FitForLifeDataManager.getInstance().getCurrentUser().getEmail())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                FitForLifeDataManager.getInstance().updateNotGoingUser2(FitForLifeDataManager.getInstance().getCurrentUser().getEmail(), id);
                                ((CalendarActivity) context).addAvaialbeEventsForAdapter(new Date(currentEvent.getTimeInMillis()));
                                addGoingNotification(FitForLifeDataManager.getInstance().getCurrentUser(), currentEvent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        });
    }




    private void notGoingOnClick(final Event currentEvent) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Event tmp = new Event(Color.YELLOW, currentEvent.getTimeInMillis(), currentEvent.getData());


                EventList.set(getPosition(currentEvent), tmp);
                button.setText("what");
                EventArrayAdapter.this.notifyDataSetChanged();

                Map<String, Object> data = new HashMap<>();
                data.put("color", Color.YELLOW);
                data.put("timeInMillis", currentEvent.getTimeInMillis());
                data.put("sessionNumber", ((Session) currentEvent.getData()).getSessionNumber());
                data.put("day", ((Session) currentEvent.getData()).getDay());
                data.put("hour", ((Session) currentEvent.getData()).getHour());
                data.put("minute", ((Session) currentEvent.getData()).getMinute());
                data.put("duration", ((Session) currentEvent.getData()).getDuration());
                data.put("groupId", ((Session) currentEvent.getData()).getGroupId());
                data.put("user1", FitForLifeDataManager.getInstance().getCurrentUser().getEmail());
                data.put("user2", null);
                fStore.collection("notGoing").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Added event to not going session " + documentReference.getId());
//                        EventArrayAdapter.this.remove(currentEvent);
//                        EventArrayAdapter.this.notifyDataSetChanged();
                        FitForLifeDataManager.getInstance().createNotGoingSession(tmp, documentReference.getId(), FitForLifeDataManager.getInstance().getCurrentUser().getEmail());
                        int count = ((CalendarActivity) context).CountMissingSessionThisMonthAdapter(new Date(currentEvent.getTimeInMillis()));
                        if (count == 1) {
                            ((CalendarActivity) context).addAvaialbeEventsForAdapter(new Date(currentEvent.getTimeInMillis()));
                            EventArrayAdapter.this.remove(tmp);
                            EventArrayAdapter.this.notifyDataSetChanged();
                            addNotGoingNotification(FitForLifeDataManager.getInstance().getCurrentUser(), currentEvent);
                        }

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document EventSession", e);
                            }
                        });
            }
        });
    }


    private void cancelSession(final Event currentEvent) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title;
                final String msg;
                title = context.getResources().getString(R.string.titleCancelSession);
                msg = context.getResources().getString(R.string.cancelSessionMsg);


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Map<String, Object> data = new HashMap<>();
                        data.put("color", currentEvent.getColor());
                        data.put("timeInMillis", currentEvent.getTimeInMillis());
                        data.put("sessionNumber", ((Session) currentEvent.getData()).getSessionNumber());
                        data.put("day", ((Session) currentEvent.getData()).getDay());
                        data.put("hour", ((Session) currentEvent.getData()).getHour());
                        data.put("minute", ((Session) currentEvent.getData()).getMinute());
                        data.put("duration", ((Session) currentEvent.getData()).getDuration());
                        data.put("groupId", ((Session) currentEvent.getData()).getGroupId());
                        fStore.collection("canceledSessions").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot Event Session written with ID: " + documentReference.getId());
                                EventArrayAdapter.this.remove(currentEvent);
                                EventArrayAdapter.this.notifyDataSetChanged();
                                FitForLifeDataManager.getInstance().createCanceledSession(currentEvent, documentReference.getId());
                                addNotification(((Session) currentEvent.getData()).getGroupId(), currentCoach, currentEvent);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document EventSession", e);
                                    }
                                });


                    }
                });

                builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });
    }
    @Override
    public int getCount() {
        return EventList.size();
    }

    @Nullable
    @Override
    public Event getItem(int position) {
        return EventList.get(position);
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
        hashMap.put("text", "Canceled session of");
        hashMap.put("text2", "");
        hashMap.put("postid", (String) formatter.format(event.getTimeInMillis()));
        hashMap.put("isEventRes", false);
        hashMap.put("isEventCanc", true);
        hashMap.put("ispost", false);
        hashMap.put("isEventNotGoing", false);
        hashMap.put("isEventGoing", false);
        hashMap.put("isProgress", false);
        reference.push().setValue(hashMap);
    }

    private void addNotGoingNotification(UserInfo currentUser, Event event) {
        Group userGroup = FitForLifeDataManager.getInstance().getGroup(currentUser.getGroupId());
        CoachInfo userCoach = FitForLifeDataManager.getInstance().getGroupCoach(userGroup);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userCoach.getId());
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy ");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", currentUser.getId());
        hashMap.put("text", "is not attending on");
        hashMap.put("text2", "Group : " + userGroup.getGroupName());
        hashMap.put("postid", (String) formatter.format(event.getTimeInMillis()));
        hashMap.put("isEventRes", false);
        hashMap.put("isEventCanc", false);
        hashMap.put("isEventNotGoing", true);
        hashMap.put("isEventGoing", false);
        hashMap.put("ispost", false);
        hashMap.put("isProgress", false);
        reference.push().setValue(hashMap);
    }

    private void addGoingNotification(UserInfo currentUser, Event event) {
        Group userGroup = FitForLifeDataManager.getInstance().getGroup(currentUser.getGroupId());
        CoachInfo userCoach = FitForLifeDataManager.getInstance().getGroupCoach(userGroup);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userCoach.getId());
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy ");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", currentUser.getId());
        hashMap.put("text", "is attending on");
        hashMap.put("text2", "Group : " + userGroup.getGroupName());
        hashMap.put("postid", (String) formatter.format(event.getTimeInMillis()));
        hashMap.put("isEventRes", false);
        hashMap.put("isEventCanc", false);
        hashMap.put("isEventNotGoing", false);
        hashMap.put("isEventGoing", true);
        hashMap.put("ispost", false);
        hashMap.put("isProgress", false);
        reference.push().setValue(hashMap);
    }
}
