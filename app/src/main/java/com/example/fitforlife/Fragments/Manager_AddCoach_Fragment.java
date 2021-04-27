package com.example.fitforlife.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitforlife.Activities.ManagerCoachTraineeGroupsActivity;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Manager_AddCoach_Fragment extends Fragment {
    private static final String TAG = "Manager_AddCoach_Fragment";
    private FirebaseAuth fAuth;
    private FirebaseAuth mAuth2;

    FirebaseFirestore fStore;
    String[] ageRange;

    private CoachInfo newCoach;
    private NumberPicker ageNumberPicker;
    private Button addCoach, cancel;
    private TextView studio;
    private EditText fullName, RegEmail, RegPassword, regConfirmPassword, regPhone;
    private String currentStudio;

    public Manager_AddCoach_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_manager__add_coach, container, false);
        Context context = getActivity();


        ageRange = context.getResources().getStringArray(R.array.ageRange);
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize_auth]

        ageNumberPicker = rootView.findViewById(R.id.newCoachAge);
        ageNumberPicker.setMaxValue(3);
        ageNumberPicker.setMinValue(0);
        ageNumberPicker.setDisplayedValues(ageRange);

        fullName = (EditText) rootView.findViewById(R.id.newCoachName);
        RegEmail = rootView.findViewById(R.id.newCoachEmail);
        RegPassword = (EditText) rootView.findViewById(R.id.newCoachPassword);
        regConfirmPassword = (EditText) rootView.findViewById(R.id.newCoachConfirmPassword);
        regPhone = rootView.findViewById(R.id.newCoachPhone);
        studio = rootView.findViewById(R.id.newCoachStudio);

        addCoach = rootView.findViewById(R.id.newCoachSave);
        cancel = rootView.findViewById(R.id.newCoachCancel);

        addCoach.setOnClickListener(addOnClick);
        cancel.setOnClickListener(cancelOnClick);


        currentStudio = FitForLifeDataManager.getInstance().getCurrentManager().getStudio();
        studio.setText(currentStudio);


        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("[fitforlife-a4308.firebaseio.com]")
                .setApiKey("AIzaSyDolrhGHAYEc7mOr2_Lnpj_bFl6ZTTo12E")
                .setApplicationId("fitforlife-a4308").build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(context.getApplicationContext(), firebaseOptions, "FitForLife");
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("FitForLife"));
        }


        return rootView;
    }


    //onclick method for save Button > adds new trainee to firebase and sqlLite
    private View.OnClickListener addOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            final String emailString = RegEmail.getText().toString().trim();
            final String passwordString = RegPassword.getText().toString();
            final String fullNameString = fullName.getText().toString();
            final String phoneString = regPhone.getText().toString();
            final String ageString = ageRange[ageNumberPicker.getValue()];

            if (!checkError(fullNameString, phoneString, ageString, emailString, passwordString)) {
                mAuth2.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //for user profile pic in realtime database
                            FirebaseUser firebaseUser = mAuth2.getCurrentUser();
                            String userID = firebaseUser.getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", userID);
                            map.put("fullName", fullNameString);
                            map.put("type", "coach");
                            map.put("status", "offline");
                            map.put("search", fullNameString.toLowerCase());
                            map.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49");
                            reference.setValue(map);

                            DocumentReference documentReference = fStore.collection("users").document(emailString);
                            final CoachInfo newCoach;
                            newCoach = new CoachInfo(mAuth2.getCurrentUser().getUid(), fullNameString, ageString, phoneString, emailString, passwordString, "coach", currentStudio);
                            documentReference.set(newCoach).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    FitForLifeDataManager.getInstance().createCoach(newCoach);
                                    ManagerCoachTraineeGroupsActivity.SetFragment(true, false, false);

                                    getActivity().onBackPressed();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                        } else {
                            // meaning user didnt auth
                            Toast.makeText(getActivity(), "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        mAuth2.signOut();
                    }
                });
            }
        }
    };


    private View.OnClickListener cancelOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // EditInfo_Fragment editInfo_fragment = new EditInfo_Fragment();
            ManagerCoachTraineeGroupsActivity.SetFragment(true, false, false);

            getActivity().onBackPressed();


        }
    };

    private Boolean checkError(String fullNameString, String phoneString, String ageString, String emailString, String passwordString) {
        boolean checkError = true;



        while (checkError) {

            boolean feError = false;

            if (TextUtils.isEmpty(fullNameString)) {
                fullName.setError("full name is Required.");
                feError = true;
            }

            if (TextUtils.isEmpty(emailString)) {
                RegEmail.setError("Email is Required.");
                feError = true;

            }

            if (TextUtils.isEmpty(passwordString)) {
                RegPassword.setError("Password is Required.");
                feError = true;

            }
            if (TextUtils.isEmpty(phoneString)) {
                regPhone.setError("Password is Required.");
                feError = true;

            }
            if (passwordString.length() < 6) {
                RegPassword.setError("Password Must be >= 6 Characters");
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
