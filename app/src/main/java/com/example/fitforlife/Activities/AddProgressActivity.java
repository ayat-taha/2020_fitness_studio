package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Measurement;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddProgressActivity extends AppCompatActivity {

    private static final String TAG = "AddProgressActivity";
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    boolean isManager;
    DatePicker date;
    private Button save, cancel;
    private TextView addProgress_FullName;
    private EditText add_weight, add_waist, add_chest, add_buttocks, add_leftArm, add_rightArm, add_leftThigh, add_rightThigh;
    private UserInfo currentUserProgress;
    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_progress);
        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

//        if(FitForLifeDataManager.getInstance().getCurrentManager()!=null && FitForLifeDataManager.getInstance().get)

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize_auth]
        currentUserProgress = FitForLifeDataManager.getInstance().getCurrentUserProgress();
        addProgress_FullName = findViewById(R.id.addProgress_FullName);
        addProgress_FullName.setText(FitForLifeDataManager.getInstance().getCurrentUserProgress().getFullName());
        add_weight = findViewById(R.id.add_weight);
        add_waist = findViewById(R.id.add_waist);
        add_chest = findViewById(R.id.add_chest);
        add_buttocks = findViewById(R.id.add_buttocks);
        add_rightArm = findViewById(R.id.add_rightArm);
        add_leftArm = findViewById(R.id.add_leftArm);
        add_rightThigh = findViewById(R.id.add_rightThigh);
        add_leftThigh = findViewById(R.id.add_leftThigh);


        save = findViewById(R.id.save_addProgress);
        saveOnClick();

        Date minDate = new Date(2010, 1, 1);
        date = findViewById(R.id.date_add_progress);
        date.setCalendarViewShown(false);
//        date.setMinDate(minDate.getTime());
//        date.setMaxDate(new Date().getTime());
        cancel = findViewById(R.id.cancel_addProgress);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProgressActivity.this, TraineeProgressActivity.class);
                startActivity(intent);
                FitForLifeDataManager.getInstance().setCurrentUserProgress(null);
            }
        });
    }


    public void saveOnClick() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkError())
                    return;

                add_weight = findViewById(R.id.add_weight);
                add_waist = findViewById(R.id.add_waist);
                add_waist = findViewById(R.id.add_waist);
                add_buttocks = findViewById(R.id.add_buttocks);
                add_rightArm = findViewById(R.id.add_rightArm);
                add_leftArm = findViewById(R.id.add_leftArm);
                add_rightThigh = findViewById(R.id.add_rightThigh);
                add_leftThigh = findViewById(R.id.add_leftThigh);

                final Calendar cal = Calendar.getInstance();
                final Double weight = Double.parseDouble(add_weight.getText().toString());
                final Double waist = Double.parseDouble(add_waist.getText().toString());
                final Double chest = Double.parseDouble(add_chest.getText().toString());
                final Double buttocks = Double.parseDouble(add_buttocks.getText().toString());
                final Double rightArm = Double.parseDouble(add_rightArm.getText().toString());
                final Double leftArm = Double.parseDouble(add_leftArm.getText().toString());
                final Double rightThigh = Double.parseDouble(add_rightThigh.getText().toString());
                final Double leftThigh = Double.parseDouble(add_leftThigh.getText().toString());

                cal.set(date.getYear(), date.getMonth(), date.getDayOfMonth());

                Map<String, Object> measurement = new HashMap<>();
                measurement.put("date", cal.getTimeInMillis());
                measurement.put("traineeId", currentUserProgress.getEmail());
                measurement.put("weight", weight);
                measurement.put("waist", waist);
                measurement.put("chest", chest);
                measurement.put("buttocks", buttocks);
                measurement.put("rightArm", rightArm);
                measurement.put("leftArm", leftArm);
                measurement.put("rightThigh", rightThigh);
                measurement.put("leftThigh", leftThigh);
                measurement.put("leftThigh", leftThigh);


                fStore.collection("users").document(currentUserProgress.getEmail()).collection("progress")
                        .add(measurement)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                Measurement newMeasurement = new Measurement(documentReference.getId(), currentUserProgress.getEmail(), cal.getTimeInMillis(), weight, waist, chest, buttocks, rightArm, leftArm, rightThigh, leftThigh);
                                FitForLifeDataManager.getInstance().createMeasurement(newMeasurement);
                                Intent intent = new Intent(AddProgressActivity.this, TraineeProgressActivity.class);
                                startActivity(intent);
                                FitForLifeDataManager.getInstance().setCurrentUserProgress(null);
                                addNotification(currentUserProgress.getId(), FitForLifeDataManager.getInstance().getCurrentCoach());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });


            }
        });
    }


    private boolean checkError() {
        boolean checkError = true;
        boolean feError = false;

        while (checkError) {
            if (TextUtils.isEmpty(add_weight.getText().toString())) {
                add_weight.setError("Please Add Weight");
                feError = true;
            }

            if (TextUtils.isEmpty(add_waist.getText().toString())) {
                add_waist.setError("Please Add Waist Measurement.");
                feError = true;

            }

            if (TextUtils.isEmpty(add_buttocks.getText().toString())) {
                add_buttocks.setError("Please Add Buttocks Measurement.");
                feError = true;
            }

            checkError = false;
        }

        return feError;

    }


    /**
     *MORE METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(AddProgressActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     *  LOGOUT METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(AddProgressActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }

    private void addNotification(String userid, CoachInfo currentCoach) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy ");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", currentCoach.getId());
        hashMap.put("text", "Added new measurements on");
        hashMap.put("text2", "");
        hashMap.put("postid", (String) formatter.format(new Date()));
        hashMap.put("isEventRes", false);
        hashMap.put("isEventCanc", false);
        hashMap.put("ispost", false);
        hashMap.put("isEventNotGoing", false);
        hashMap.put("isEventGoing", false);
        hashMap.put("isProgress", true);
        hashMap.put("isPayment", false);
        reference.push().setValue(hashMap);
    }

}// END ADDPROGRESSACTIVITY

