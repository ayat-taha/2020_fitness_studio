package com.example.fitforlife.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fitforlife.Model.Session;
import com.example.fitforlife.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class sessionAdapter extends ArrayAdapter<Session> {

    private static final String TAG = "sessionAdapter";

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private List<Session> sessionList;
    private Context context;
    private ImageView deleteSession;
    private TextView sessionNumber;
    private TextView sessionDay;
    private TextView sessionDuration;
    private TextView sessionHour;
    private TextView sessionMinute;


    public sessionAdapter(Context context, int resource, List<Session> data) {
        super(context, resource, data);
        this.sessionList = data;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_session, null, false);
        final Session currentItem = getItem(position);

        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        final String[] weekDays = context.getResources().getStringArray(R.array.weekDays);
        final String[] weekDaysEn = context.getResources().getStringArray(R.array.weekDaysEn);

        sessionNumber = rootView.findViewById(R.id.session_number);
        sessionNumber.setText(Integer.toString(position + 1));

        sessionDay = rootView.findViewById(R.id.session_day);
        int dayIndex =0;
        for(int i =0; i< weekDays.length ; i++){
            Log.d("dayofweek", " day:" + weekDays[i]);
            if(weekDaysEn[i].equals(currentItem.getDay()))
                dayIndex=i;
        }
        sessionDay.setText(weekDays[dayIndex]);

        sessionDuration = rootView.findViewById(R.id.session_duration);
        sessionDuration.setText(String.valueOf(currentItem.getDuration()));

        sessionHour = rootView.findViewById(R.id.session_hour);
        sessionMinute = rootView.findViewById(R.id.session_minute);
        sessionHour.setText("" + currentItem.getHour());
        sessionMinute.setText("" + currentItem.getMinute());


        deleteSession = rootView.findViewById(R.id.deleteSession);
        deleteSession.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String title;
                final String msg;
                title = context.getResources().getString(R.string.deleteSession);
                msg = context.getResources().getString(R.string.deleteSessionMsg);


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        sessionAdapter.this.remove(currentItem);
                        sessionAdapter.this.notifyDataSetChanged();
                        Log.d(TAG, "currentItem.getGroupId()" + currentItem.getGroupId());
                        if (currentItem.getGroupId() != null) {
                            DocumentReference docRef = fStore.collection("groups").document(currentItem.getGroupId()).collection("sessions").document(currentItem.getSessionNumber());

                            docRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                        }
                                    });

                        }


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


        return rootView;
    }
}
