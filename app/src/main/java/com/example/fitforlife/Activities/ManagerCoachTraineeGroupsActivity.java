package com.example.fitforlife.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.fitforlife.Fragments.Groups_Fragment;
import com.example.fitforlife.Fragments.Manager_CoachFragmenet;
import com.example.fitforlife.Fragments.Trainee_Fragment;
import com.example.fitforlife.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ManagerCoachTraineeGroupsActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton groupsRadio, coachesRadio, traineeRadio;
    private BottomNavigationView bottomNavigationView;
    private static boolean traineeF;
    private static boolean groupF;
    private static boolean coachF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_coach_trainee_groups_acitvity);

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        // Bottom Navigation Bar - action Listener
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        setNav();

        radioGroup = findViewById(R.id.radioGroup_manager);
        coachesRadio = findViewById(R.id.radioCoaches);
        traineeRadio = findViewById(R.id.radioTrainee);
        groupsRadio = findViewById(R.id.radioGroups);

        coachesRadio.setChecked(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Manager_CoachFragmenet coacheFragmenet = new Manager_CoachFragmenet();
        fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, coacheFragmenet).commit();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                if (checkedId == R.id.radioCoaches) {
                    Manager_CoachFragmenet coachesFragment = new Manager_CoachFragmenet();
                    fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, coachesFragment).commit();
                    Toast.makeText(ManagerCoachTraineeGroupsActivity.this, getResources().getString(R.string.coaches), Toast.LENGTH_SHORT).show();
                    traineeF = false;
                    groupF = false;
                    coachF = true;
                    return;
                }
                if (checkedId == R.id.radioTrainee) {
                    traineeF = true;
                    groupF = false;
                    coachF = false;
                    Trainee_Fragment TraineeFragment = new Trainee_Fragment(true);
                    fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, TraineeFragment).commit();
                    Toast.makeText(ManagerCoachTraineeGroupsActivity.this, getResources().getString(R.string.trainee), Toast.LENGTH_SHORT).show();

                }
                if (checkedId == R.id.radioGroups) {
                    traineeF = false;
                    groupF = true;
                    coachF = false;
                    Groups_Fragment groupsFragment = new Groups_Fragment(true);
                    fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, groupsFragment).commit();

                }
            }
        });
    }


    /**
     * OnClickMore METHOD
     *
     * @param - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(ManagerCoachTraineeGroupsActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(ManagerCoachTraineeGroupsActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();
    }


    @Override
    public void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (radioGroup.getCheckedRadioButtonId() == coachesRadio.getId()) {
            Manager_CoachFragmenet CoachFragment = new Manager_CoachFragmenet();
            fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, CoachFragment).commit();
        }
        if (radioGroup.getCheckedRadioButtonId() == traineeRadio.getId()) {
            Trainee_Fragment traineeFragment = new Trainee_Fragment(true);
            fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, traineeFragment).commit();
        }
        if (radioGroup.getCheckedRadioButtonId() == groupsRadio.getId()) {
            Groups_Fragment GroupFragment = new Groups_Fragment(true);
            fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, GroupFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (traineeF) {
            Trainee_Fragment traineeFragment = new Trainee_Fragment(true);
            fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, traineeFragment).addToBackStack(null).commit();
        }
        if (groupF) {
            Groups_Fragment groupFragment = new Groups_Fragment(true);
            fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, groupFragment).addToBackStack(null).commit();
        }
        if (coachF) {
            Manager_CoachFragmenet coachesFragment = new Manager_CoachFragmenet();
            fragmentManager.beginTransaction().replace(R.id.content_CoachTraineeGroup, coachesFragment).addToBackStack(null).commit();
        }

    }

    public static void SetFragment(boolean isCoach, boolean isTrainee, boolean isGroup) {

        ManagerCoachTraineeGroupsActivity.coachF = isCoach;
        ManagerCoachTraineeGroupsActivity.traineeF = isTrainee;
        ManagerCoachTraineeGroupsActivity.groupF = isGroup;
    }


    /**
     * set navigation on click
     */
    private void setNav() {
        bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calendar:

                        Intent calendarIntent = new Intent(ManagerCoachTraineeGroupsActivity.this, ManagerCalendarAcitvity.class);
                        startActivity(calendarIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.progress:
                        Intent Progress = new Intent(ManagerCoachTraineeGroupsActivity.this, TraineeProgressActivity.class);
                        startActivity(Progress);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        Intent homeIntent = new Intent(ManagerCoachTraineeGroupsActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.payments:
                        Intent paymentsIntent = new Intent(ManagerCoachTraineeGroupsActivity.this, TraineePaymentsActivity.class);
                        startActivity(paymentsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        Intent groupsIntent = new Intent(ManagerCoachTraineeGroupsActivity.this, ManagerCoachTraineeGroupsActivity.class);
                        startActivity(groupsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        // END NAVIGATION BAR
    }


} // END ACTIVITY