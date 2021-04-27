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
import com.example.fitforlife.Activities.ManagerCalendarAcitvity;
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

public class ManagerEventAdapter extends ArrayAdapter<Event> {

    private static final String TAG = "EventArrayAdapter";

    private List<Event> EventList;
    private Context context;
    private Activity ctx;
    private Button button;
    private Event currentEventTest;
    private CoachInfo currentManager;

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    public ManagerEventAdapter(Context context, int resource, List<Event> data) {
        super(context, resource, data);
        this.EventList = data;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize_auth]


        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.manager_card_events, null, false);
        currentEventTest = getItem(position);
        currentManager = FitForLifeDataManager.getInstance().getCurrentManager();
        final Event currentEvent = getItem(position);
        TextView vDate = v.findViewById(R.id.date_event);
        TextView vTime = v.findViewById(R.id.time_event);
        TextView vduration = v.findViewById(R.id.duration_event);
        TextView vgroup = v.findViewById(R.id.group_event);
        TextView vcoach = v.findViewById(R.id.coach_event);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy ");
        DateFormat formatter2 = new SimpleDateFormat("HH:mm  ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentEvent.getTimeInMillis());
        vDate.setText(formatter.format(calendar.getTime()));
        vTime.setText(formatter2.format(calendar.getTime()));
        vduration.setText("" + ((Session) (currentEvent.getData())).getDuration());
        Group groupName = FitForLifeDataManager.getInstance().getGroup(((Session) (currentEvent.getData())).getGroupId());
        CoachInfo coach = FitForLifeDataManager.getInstance().getCoach(groupName.getCoachId());
        vgroup.setText(groupName.getGroupName());
        vcoach.setText(coach.getFullName());

        button = v.findViewById(R.id.managerCancel);
        Date today = new Date();
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(today);

        if (currentEvent.getTimeInMillis() < firstDay.getTimeInMillis()) {
            final Event tmp = new Event(Color.RED, currentEvent.getTimeInMillis(), currentEvent.getData());
            EventList.set(getPosition(currentEvent), tmp);
            ManagerEventAdapter.this.notifyDataSetChanged();
            button.setVisibility(View.GONE);

        }

        if (currentEvent.getColor() == Color.GREEN || currentEvent.getColor() == Color.BLACK) {
            long hourInMillis = 3600000;
            if (currentEvent.getTimeInMillis() - firstDay.getTimeInMillis() > hourInMillis * 2) {
                button.setText(context.getResources().getText(R.string.cancelSession));
                cancelSession(currentEvent);
            }
        }

        return v;
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
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
                                ManagerEventAdapter.this.remove(currentEvent);
                                ManagerEventAdapter.this.notifyDataSetChanged();
                                FitForLifeDataManager.getInstance().createCanceledSession(currentEvent, documentReference.getId());
                                ((ManagerCalendarAcitvity) context).deleteCanceled(currentEvent);
                                addNotification(((Session) currentEvent.getData()).getGroupId(), currentManager, currentEvent);


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

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });
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
    @Override
    public int getCount() {
        return EventList.size();
    }

    @Nullable
    @Override
    public Event getItem(int position) {
        return EventList.get(position);
    }
}
