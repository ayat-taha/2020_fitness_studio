package com.example.fitforlife.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fitforlife.Activities.GroupsTraineeActivity;
import com.example.fitforlife.Activities.ManagerCoachTraineeGroupsActivity;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class Manager_EditCoachFragment extends Fragment {

    private static final String TAG = "Manager_EditCoachFragment";
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private String[] ageRange;
    private EditText fullName, phone;
    private TextView email, studio;
    private Spinner groupSpinner;
    private NumberPicker agePicker;
    private CoachInfo currentCoach;
    private DatabaseReference mDatabase;

    public Manager_EditCoachFragment(CoachInfo currentItem) {
        // Required empty public constructor
        this.currentCoach = currentItem;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_manager__edit_coach, container, false);
        Context context = getActivity();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]
        fullName = rootView.findViewById(R.id.EditCoach_FullName);
        fullName.setText(currentCoach.getFullName());

        phone = rootView.findViewById(R.id.EditCoach_phone);
        phone.setText(currentCoach.getPhone());

        email = rootView.findViewById(R.id.EditCoach_email);
        email.setText(currentCoach.getEmail());

        studio = rootView.findViewById(R.id.editCoach_studio);
        studio.setText(currentCoach.getStudio());

        ageRange = context.getResources().getStringArray(R.array.ageRange);
        agePicker = rootView.findViewById(R.id.EditCoach_age);
        agePicker.setMaxValue(3);
        agePicker.setMinValue(0);
        agePicker.setDisplayedValues(ageRange);
        for (int i = 0; i < ageRange.length; i++) {
            if (ageRange[i].equals(currentCoach.getAge()))
                agePicker.setValue(i);
        }

        Button save = rootView.findViewById(R.id.save_EditCoach);
        save.setOnClickListener(SaveEditOnClick);

        Button cancel = (Button) rootView.findViewById(R.id.cancel_EditCoach);
        cancel.setOnClickListener(CancelEditOnClick);
        return rootView;
    }

    private View.OnClickListener CancelEditOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            ManagerCoachTraineeGroupsActivity.SetFragment(true, false, false);
            getActivity().onBackPressed();


        }
    };


    private View.OnClickListener SaveEditOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            final String fullNameString = fullName.getText().toString();
            final String phoneString = phone.getText().toString();
            final String ageString = ageRange[agePicker.getValue()];
            if (!checkError()) {
                currentCoach.setFullName(fullNameString);
                currentCoach.setPhone(phoneString);
                currentCoach.setAge(ageString);

                final DocumentReference documentReference = fStore.collection("users").document(currentCoach.getEmail());
                documentReference.set(currentCoach).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FitForLifeDataManager.getInstance().updateCoach(currentCoach);
                        mDatabase.child("Users").child(currentCoach.getId()).child("fullName").setValue(currentCoach.getFullName());
                        mDatabase.child("Users").child(currentCoach.getId()).child("search").setValue(currentCoach.getFullName().toLowerCase());


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

                ManagerCoachTraineeGroupsActivity.SetFragment(true, false, false);
                getActivity().onBackPressed();
            }
        }
    };

    private Boolean checkError() {
        String fullNameString = fullName.getText().toString();
        String phoneString = phone.getText().toString();

        boolean checkError = true;

        while (checkError) {

            boolean feError = false;

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
