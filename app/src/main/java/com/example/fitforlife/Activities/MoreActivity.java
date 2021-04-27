package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;



public class MoreActivity extends AppCompatActivity {
    private static final String TAG = "MoreActivity";

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private ImageView profilePicture;

    private Button profileBtn, rescheduleBtn, aboutBtn, navBtn, langBtn, reportBtn, messageBtn, homeBtn,waze;
    private TextView userName;
    //    String currentUser;
    private Boolean isTrainee;
    private Boolean isManager;
    private UserInfo currentUser;
    private CoachInfo currentManager;
    private CoachInfo currentCoach;
    private String studioName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

//        Calendar firstDayOfMonth = Calendar.getInstance();
//        firstDayOfMonth.set(Calendar.HOUR_OF_DAY, 00);
//        firstDayOfMonth.set(Calendar.MINUTE, 00);
//        firstDayOfMonth.set(Calendar.SECOND, 00);
//        firstDayOfMonth.set(Calendar.MILLISECOND, 00);
//        firstDayOfMonth.set(Calendar.YEAR,2020);
//        firstDayOfMonth.set(Calendar.MONTH,1);
//        firstDayOfMonth.set(Calendar.DAY_OF_MONTH,1);
//
//        Calendar lastDayOfMonth = Calendar.getInstance();
//        lastDayOfMonth.set(Calendar.HOUR_OF_DAY, 23);
//        lastDayOfMonth.set(Calendar.MINUTE, 59);
//        lastDayOfMonth.set(Calendar.SECOND, 59);
//        lastDayOfMonth.set(Calendar.MILLISECOND, 59);
//        lastDayOfMonth.set(Calendar.YEAR,2020);
//        lastDayOfMonth.set(Calendar.MONTH,1);
//        int res = lastDayOfMonth.getActualMaximum(Calendar.DATE);
//        lastDayOfMonth.set(Calendar.DAY_OF_MONTH,res);
//List<UserInfo> tmp = FitForLifeDataManager.getInstance().getAllJoinedCoach(FitForLifeDataManager.getInstance().getCurrentCoach(),11,2020);
//        Log.d(TAG, "onCreate: lastday time in millis " + lastDayOfMonth.getTimeInMillis() + "fistrs " + firstDayOfMonth.getTimeInMillis()+ " last day number "+res);
//        Log.d(TAG, "list " + tmp);


        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        userName = findViewById(R.id.userName);
        profileBtn = findViewById(R.id.coachProfileButton);
        rescheduleBtn = findViewById(R.id.rescheduleButton);
   //     langBtn = findViewById(R.id.languageButton);
        reportBtn = findViewById(R.id.reportBtn);
        messageBtn = findViewById(R.id.messageBtn);
        profilePicture = findViewById(R.id.MoreProfilePic);
        aboutBtn = findViewById(R.id.aboutButton);
        navBtn = findViewById(R.id.navigationButton);
        homeBtn = findViewById(R.id.home);

        waze = findViewById(R.id.wazebtn);

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        //current is **** Coach ****
        if (FitForLifeDataManager.getInstance().getCurrentCoach() != null && FitForLifeDataManager.getInstance().getCurrentUser() == null && FitForLifeDataManager.getInstance().getCurrentManager() == null) {
            currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
            studioName = currentCoach.getStudio();
            userName.setText(currentCoach.getFullName());
            isTrainee = false;
            isManager = false;
            getUserInfo(profilePicture, currentCoach.getId());
            rescheduleBtn.setOnClickListener(rescheduleOnClick);

            // current is **** Trainee ****
        } else if (FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentUser() != null && FitForLifeDataManager.getInstance().getCurrentManager() == null) {
            currentUser = FitForLifeDataManager.getInstance().getCurrentUser();
            userName.setText(currentUser.getFullName());
            studioName = currentUser.getStudio();

            isTrainee = true;
            isManager = false;
            getUserInfo(profilePicture, currentUser.getId());
            rescheduleBtn.setVisibility(View.GONE);
            reportBtn.setVisibility(View.GONE);

            // current is **** MANAGER ****
        } else if (FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentUser() == null && FitForLifeDataManager.getInstance().getCurrentManager() != null) {
            currentManager = FitForLifeDataManager.getInstance().getCurrentManager();
            studioName = currentManager.getStudio();
            userName.setText(currentManager.getFullName());
            isTrainee = false;
            isManager = true;
            getUserInfo(profilePicture, currentManager.getId());
            rescheduleBtn.setOnClickListener(rescheduleOnClick);
        }

        // buttons on click listener
        profileBtn.setOnClickListener(profileOnClick);
        reportBtn.setOnClickListener(reportOnClick);
        messageBtn.setOnClickListener(messageOnClick);
        aboutBtn.setOnClickListener(aboutOnClick);
        navBtn.setOnClickListener(navOnClick);
        waze.setOnClickListener(wazeOnClick);
        homeBtn.setOnClickListener(HomeOnClick);
//        langBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                BottomSheetDialog btm = new BottomSheetDialog(
//                        MoreActivity.this, R.style.BottomSheetDialogTheme
//                );
//                View btsv = LayoutInflater.from(getApplicationContext())
//                        .inflate(R.layout.layout_bottom_sheet, (LinearLayout) findViewById(R.id.bottomSheetContainer));
//
//                btm.setContentView(btsv);
//                btm.show();
//            }
//
//        });
    }

    private View.OnClickListener profileOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent profile = new Intent(MoreActivity.this, ProfileActivity.class);
            startActivity(profile);
        }
    };
    private View.OnClickListener HomeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent profile = new Intent(MoreActivity.this, HomeActivity.class);
            startActivity(profile);
        }
    };

    private View.OnClickListener messageOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent profile = new Intent(MoreActivity.this, MessagesActivity.class);
            startActivity(profile);
        }
    };

    private View.OnClickListener reportOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent profile = new Intent(MoreActivity.this, ReportsActivity.class);
            startActivity(profile);
        }
    };

    private View.OnClickListener rescheduleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent profile = new Intent(MoreActivity.this, RescheduleActivity.class);
            startActivity(profile);
        }
    };

    private void getUserInfo(final ImageView imageView, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * OnClickMore METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(MoreActivity.this, HomeActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(MoreActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }


    private View.OnClickListener aboutOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent about = new Intent(MoreActivity.this, AboutActivity.class);
            startActivity(about);
        }
    };

    private View.OnClickListener navOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", 33.021729, 35.446461);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }
    };

    private View.OnClickListener wazeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {

            final DocumentReference docRef = fStore.collection("studios").document(studioName);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            if (document.get("waze") != null)
                                openWaze(document.get("waze").toString());
                        } else {
                            Log.d(TAG, "No such document");
                            Toast.makeText(MoreActivity.this, getResources().getString(R.string.noLocation), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(MoreActivity.this, getResources().getString(R.string.somethingWentWrong), Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }
    };

    private void openWaze(String waze) {

        try {
            // Launch Waze to look for Hawaii:

            String url = waze;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // If Waze is not installed, open it in Google Play:
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
            startActivity(intent);
        }
    }

}
