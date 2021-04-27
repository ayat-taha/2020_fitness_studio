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
import com.example.fitforlife.R;
import com.example.fitforlife.Fragments.Trainee_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class GroupsTraineeActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton groupsRadio, traineeRadio;
    private BottomNavigationView bottomNavigationView;
    public static boolean isTraineeFragment = true;


    final boolean isCoach = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_trainee);

        // setting custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));


        // Bottom Navigation Bar - action Listener
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        setNav();


        radioGroup = (RadioGroup) findViewById(R.id.radioButon_groupTrainee);
        traineeRadio = findViewById(R.id.radioTrainee);
        groupsRadio = findViewById(R.id.radioGroups);
        final int selectedId = radioGroup.getCheckedRadioButtonId();

        RadioButton rb = (RadioButton) findViewById(R.id.radioTrainee);
        rb.setChecked(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Trainee_Fragment traineeFragment = new Trainee_Fragment(false);
        fragmentManager.beginTransaction().replace(R.id.content_groupsTrainee, traineeFragment).commit();
       // Toast.makeText(GroupsTraineeActivity.this, "Trainee", Toast.LENGTH_SHORT).show();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (checkedId == R.id.radioTrainee) {
                    Trainee_Fragment traineeFragment = new Trainee_Fragment(false);
                    fragmentManager.beginTransaction().replace(R.id.content_groupsTrainee, traineeFragment).addToBackStack(null).commit();
                 //   Toast.makeText(GroupsTraineeActivity.this, "Trainee", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkedId == R.id.radioGroups) {
                    Groups_Fragment groupsFragment = new Groups_Fragment(false);
                    fragmentManager.beginTransaction().replace(R.id.content_groupsTrainee, groupsFragment).addToBackStack(null).commit();

                } else
                    Toast.makeText(GroupsTraineeActivity.this, "You must choose option  ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNav() {
        bottomNavigationView.getMenu().getItem(4).setIcon(R.drawable.ic_group);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.calendar:
                        Intent calendarIntent = new Intent(GroupsTraineeActivity.this, CalendarActivity.class);
                        startActivity(calendarIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.progress:
                        Intent Progress = new Intent(GroupsTraineeActivity.this, TraineeProgressActivity.class);
                        startActivity(Progress);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        Intent homeIntent = new Intent(GroupsTraineeActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.payments:
                        Intent paymentsIntent = new Intent(GroupsTraineeActivity.this, TraineePaymentsActivity.class);
                        startActivity(paymentsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        Intent groupsIntent = new Intent(GroupsTraineeActivity.this, GroupsTraineeActivity.class);
                        startActivity(groupsIntent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        // END NAVIGATION BAR

    }


    /**
     * OnClickMore METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickMore(View view) {
        Intent MoreIntent = new Intent(GroupsTraineeActivity.this, MoreActivity.class);
        startActivity(MoreIntent);
    }

    /**
     * OnClickLogOut METHOD
     *
     * @param view - IMAGE VIEW
     */
    public void OnClickLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent MoreIntent = new Intent(GroupsTraineeActivity.this, MainActivity.class);
        startActivity(MoreIntent);
        finish();

    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (radioGroup.getCheckedRadioButtonId() == traineeRadio.getId()) {
            Trainee_Fragment traineeFragment = new Trainee_Fragment(false);
            fragmentManager.beginTransaction().replace(R.id.content_groupsTrainee, traineeFragment).commit();
        }
        if (radioGroup.getCheckedRadioButtonId() == groupsRadio.getId()) {
            Groups_Fragment GroupFragment = new Groups_Fragment(false);
            fragmentManager.beginTransaction().replace(R.id.content_groupsTrainee, GroupFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (isTraineeFragment) {
            Trainee_Fragment traineeFragment = new Trainee_Fragment(false);
            fragmentManager.beginTransaction().replace(R.id.content_groupsTrainee, traineeFragment).addToBackStack(null).commit();
        } else {
            Groups_Fragment groupFragment = new Groups_Fragment(false);
            fragmentManager.beginTransaction().replace(R.id.content_groupsTrainee, groupFragment).addToBackStack(null).commit();
        }
    }

    public static void SetFragment(boolean isTraineeF) {

        GroupsTraineeActivity.isTraineeFragment = isTraineeF;
    }

}
