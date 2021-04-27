package com.example.fitforlife.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitforlife.Activities.GroupsTraineeActivity;
import com.example.fitforlife.Activities.ManagerCoachTraineeGroupsActivity;
import com.example.fitforlife.Adapters.GoalAdapter;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditTrainee_Fragment extends Fragment {
    // should Group Object
    private static final String TAG = "EditTrainee_Fragment";
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private UserInfo currentTrainee;
    String[] ageRange;
    private EditText fullName, phone;
    private TextView email;
    private Spinner groupSpinner;
    private NumberPicker agePicker;
    private boolean isManager;
    private DatabaseReference mDatabase;
    private Context context;
    BottomSheetDialog btm;
    View btsv;
    GoalAdapter adapter;
    TextView weeklyGoal;
    EditText goalWeight;
    List<String> goalsOptions;


    public EditTrainee_Fragment(UserInfo currentTrainee) {
        // Required empty public constructor
        this.currentTrainee = currentTrainee;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_edit_trainee, container, false);
        context = getActivity();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        goalWeight = rootView.findViewById(R.id.goalWeight);
        goalWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        weeklyGoal = rootView.findViewById(R.id.weeklyGoal);
        goalsOptions = new ArrayList<>();
        goalsOptions = Arrays.asList(context.getResources().getStringArray(R.array.goals));

        if (currentTrainee != null) {

            if (currentTrainee.getWeightGoal() != null) {
                goalWeight.setText("" + currentTrainee.getWeightGoal());
            }
            if (currentTrainee.getWeeklyGoal() != null)
                weeklyGoal.setText(currentTrainee.getWeeklyGoal());
            weeklyGoal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                String tmp="lose1Icon";
//                int resID = getResources().getIdentifier(tmp, "id", "com.example.fitforlife");

                    btm = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                    btsv = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.layout_bottom_sheet, (LinearLayout) rootView.findViewById(R.id.bottomSheetContainer));
//                final ImageView image=btsv.findViewById(resID);
//                image.setVisibility(View.VISIBLE);


                    adapter = new GoalAdapter(context, R.layout.card_goal, goalsOptions);
                    final ListView goalList = btsv.findViewById(R.id.listGoalOptions);
                    goalList.setAdapter(adapter);

                    if (currentTrainee.getWeeklyGoal() != null) {
                        adapter.SetSelectedPos(goalsOptions.indexOf(currentTrainee.getWeeklyGoal()));
                    }
                    btm.setContentView(btsv);
                    btm.show();

                    goalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            currentTrainee.setWeeklyGoal(goalsOptions.get(position));
                            weeklyGoal.setText(goalsOptions.get(position));
                            adapter.SetSelectedPos(position);
                            adapter.notifyDataSetChanged();
                            btm.dismiss();
                        }
                    });
                }

            });

        }

        ArrayList<Group> groupsForSpinner = new ArrayList<>();
        //current is manager
        if (FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentManager() != null) {
            isManager = true;
            groupsForSpinner = (ArrayList<Group>) FitForLifeDataManager.getInstance().getAllManagerGroups(FitForLifeDataManager.getInstance().getCurrentManager());


        }
        //CURRENT IS COACH
        if (FitForLifeDataManager.getInstance().getCurrentCoach() != null && FitForLifeDataManager.getInstance().getCurrentManager() == null) {
            isManager = false;
            groupsForSpinner = (ArrayList<Group>) FitForLifeDataManager.getInstance().getAllCoachGroup(FitForLifeDataManager.getInstance().getCurrentCoach().getEmail());

        }

        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        fullName = rootView.findViewById(R.id.EditTrainee_FullName);
        fullName.setText(currentTrainee.getFullName());

        phone = rootView.findViewById(R.id.editTrainee_phone);
        phone.setText(currentTrainee.getPhone());

        email = rootView.findViewById(R.id.editTrainee_email);
        email.setText(currentTrainee.getEmail());

        ageRange = context.getResources().getStringArray(R.array.ageRange);
        agePicker = rootView.findViewById(R.id.EditTrainee_age);
        agePicker.setMaxValue(3);
        agePicker.setMinValue(0);
        agePicker.setDisplayedValues(ageRange);
        for (int i = 0; i < ageRange.length; i++) {
            if (ageRange[i].equals(currentTrainee.getAge()))
                agePicker.setValue(i);
        }


        groupSpinner = rootView.findViewById(R.id.EditTrainee_groupNameSpinner);

        Log.d(TAG, "is group array emty " + groupsForSpinner);
        ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(context, R.layout.simple_spinner_item, groupsForSpinner);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);
        int spinnerPosition = groupsForSpinner.indexOf(FitForLifeDataManager.getInstance().getGroup(currentTrainee.getGroupId()));
        if (spinnerPosition >= 0)
            groupSpinner.setSelection(spinnerPosition);

        Button save = rootView.findViewById(R.id.save_editTrainee);
        save.setOnClickListener(SaveEditOnClick);

        Button cancel = (Button) rootView.findViewById(R.id.cancel_editTrainee);
        cancel.setOnClickListener(CancelEditOnClick);

        return rootView;

    }


    private View.OnClickListener CancelEditOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {

            if (isManager)
                ManagerCoachTraineeGroupsActivity.SetFragment(false, true, false);
            else
                GroupsTraineeActivity.SetFragment(true);
            getActivity().onBackPressed();
        }
    };


    private View.OnClickListener SaveEditOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            final String fullNameString = fullName.getText().toString();
            final String phoneString = phone.getText().toString();
            final String ageString = ageRange[agePicker.getValue()];
            final String weightGoal = goalWeight.getText().toString();
            final String weeklyGoalString = weeklyGoal.getText().toString();

            if (!checkError()) {
                if (groupSpinner.getSelectedItem() != null)
                    currentTrainee.setGroupId(((Group) groupSpinner.getSelectedItem()).getGroupId());
                currentTrainee.setFullName(fullNameString);
                currentTrainee.setPhone(phoneString);
                currentTrainee.setAge(ageString);
                currentTrainee.setWeightGoal(Double.parseDouble(goalWeight.getText().toString()));
                currentTrainee.setWeeklyGoal(weeklyGoalString);

                final DocumentReference documentReference = fStore.collection("users").document(currentTrainee.getEmail());
                documentReference.set(currentTrainee).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: update group " + documentReference.getId());
                        FitForLifeDataManager.getInstance().updateUser(currentTrainee);
                        Log.d(TAG, "current user set password ? " + currentTrainee.getPassword());
                        mDatabase.child("Users").child(currentTrainee.getId()).child("fullName").setValue(currentTrainee.getFullName());
                        mDatabase.child("Users").child(currentTrainee.getId()).child("search").setValue(currentTrainee.getFullName().toLowerCase());


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });

                if (isManager)
                    ManagerCoachTraineeGroupsActivity.SetFragment(false, true, false);
                else
                    GroupsTraineeActivity.SetFragment(true);
                getActivity().onBackPressed();
            }
        }
    };


    private Boolean checkError() {
        String fullNameString = fullName.getText().toString();
        String phoneString = phone.getText().toString();
        final String weightGoal = goalWeight.getText().toString();
        final String weeklyGoalString = weeklyGoal.getText().toString();
        boolean checkError = true;

        while (checkError) {

            boolean feError = false;

            if (TextUtils.isEmpty(weightGoal)) {
                goalWeight.setError("Weight Goal is Required.");
                feError = true;

            }
            if (TextUtils.isEmpty(weeklyGoalString)) {
                weeklyGoal.setError("weekly Goal is Required.");
                feError = true;
            }
            if (TextUtils.isEmpty(fullNameString)) {
                fullName.setError("full Name Is required");
                feError = true;
            }
            if (TextUtils.isEmpty(phoneString)) {
                phone.setError("phone number Is required");
                feError = true;
            }

            if (feError)
                return true;
            else
                checkError = false;
        }
        return false;
    }

}


