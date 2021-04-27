package com.example.fitforlife.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.anychart.core.utils.LegendItemSettings;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.R;
import com.example.fitforlife.Adapters.RescheduleAdapter;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RescheduleActivity extends AppCompatActivity {

    private static final String TAG = "RescheduleActivity";
    private ListView listView;
    private CoachInfo currentCoach;
    private boolean isManager;
    private List<Event> canceledEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule);

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        canceledEvents = new ArrayList<>();
        listView = findViewById(R.id.rescheduleList);
        // current user is manager
        if (FitForLifeDataManager.getInstance().getCurrentManager() != null && FitForLifeDataManager.getInstance().getCurrentCoach() == null) {
            currentCoach = FitForLifeDataManager.getInstance().getCurrentManager();
            isManager = true;
//            canceledEvents=FitForLifeDataManager.getInstance().getAllManagerCanceled(currentCoach);

            canceledEvents = FitForLifeDataManager.getInstance().getAllManagerCanceledToReschedule(currentCoach);
            Log.d(TAG, "canceledEvents : " + canceledEvents);

        }
        // current user is coach
        if (FitForLifeDataManager.getInstance().getCurrentManager() == null && FitForLifeDataManager.getInstance().getCurrentCoach() != null) {
            currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
            isManager = false;
            List<Group> coachGroups = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
            Log.d(TAG, "coachGroups  " + coachGroups);
            for (Group tmp : coachGroups) {
                if (!FitForLifeDataManager.getInstance().getAllCanceledByGroupId(tmp.getGroupId()).isEmpty())
                    canceledEvents.addAll(FitForLifeDataManager.getInstance().getAllCanceledByGroupId(tmp.getGroupId()));
                Log.d(TAG, "canceled " + FitForLifeDataManager.getInstance().getAllCanceledByGroupId(tmp.getGroupId()));
            }
        }


        RescheduleAdapter rescheduleAdapter = new RescheduleAdapter(this, R.layout.card_reschedule, canceledEvents);
        listView.setAdapter(rescheduleAdapter);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (isManager)
//            canceledEvents = FitForLifeDataManager.getInstance().getAllManagerCanceledToReschedule(currentCoach);
//        if (!isManager) {
//            List<Group> coachGroups = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
//            for (Group tmp : coachGroups) {
//                canceledEvents.addAll(FitForLifeDataManager.getInstance().getAllCanceledByGroupId(tmp.getGroupId()));
//            }
//            RescheduleAdapter rescheduleAdapter = new RescheduleAdapter(this, R.layout.card_reschedule, canceledEvents);
//            listView.setAdapter(rescheduleAdapter);
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isManager)
            canceledEvents = FitForLifeDataManager.getInstance().getAllManagerCanceledToReschedule(currentCoach);
        if (!isManager) {
            List<Group> coachGroups = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
            for (Group tmp : coachGroups) {
                canceledEvents.addAll(FitForLifeDataManager.getInstance().getAllCanceledByGroupId(tmp.getGroupId()));
            }
            RescheduleAdapter rescheduleAdapter = new RescheduleAdapter(this, R.layout.card_reschedule, canceledEvents);
            listView.setAdapter(rescheduleAdapter);
            rescheduleAdapter.notifyDataSetChanged();
        }

    }

//    @Override
////    protected void onResume() {
////        super.onResume();
////        if (isManager)
////            canceledEvents = FitForLifeDataManager.getInstance().getAllManagerCanceledToReschedule(currentCoach);
////        if (!isManager) {
////            List<Group> coachGroups = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
////            for (Group tmp : coachGroups) {
////                canceledEvents.addAll(FitForLifeDataManager.getInstance().getAllCanceledByGroupId(tmp.getGroupId()));
////            }
////            RescheduleAdapter rescheduleAdapter = new RescheduleAdapter(this, R.layout.card_reschedule, canceledEvents);
////            listView.setAdapter(rescheduleAdapter);
////            rescheduleAdapter.notifyDataSetChanged();
////        }
////
////    }
//
//
//    /**
//     * logout method
//     *
//     * @param view - logout icon
//     */
//    public void OnClickLogOut(View view) {
//        FirebaseAuth.getInstance().signOut();
//        Intent MoreIntent = new Intent(RescheduleActivity.this, MainActivity.class);
//        startActivity(MoreIntent);
//        finish();
//    }

    /**
     * More METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(RescheduleActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }
}
