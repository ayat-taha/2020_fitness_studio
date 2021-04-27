package com.example.fitforlife.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditInfo_Fragment extends Fragment {
    private static final String TAG = "EditInfo_Fragment";

    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    EditText fullName, Email, password, confPassword, phone;
    String newFullName, newAge, newPhone;
    String prevPass;
    NumberPicker age;
    String[] ageRange = {"Under 13", "13-18", "19-30", "30+"};

    private DatabaseReference mDatabase;

    private UserInfo currentUser;
    private CoachInfo currentCoach;
    private boolean isCoach, isManager;
    Button saveEdit, cancelEdit;


    public EditInfo_Fragment(boolean isCoach, boolean isManager) {
        this.isCoach = isCoach;
        this.isManager = isManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_edit_info, container, false);

        // Initialize Firebase Auth & FireStore
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // [END initialize]
        mDatabase = FirebaseDatabase.getInstance().getReference();


        fullName = rootView.findViewById(R.id.editFullname);
        Email = rootView.findViewById(R.id.editEmail);
        password = rootView.findViewById(R.id.editPassword);
        confPassword = rootView.findViewById(R.id.editConfirmPass);
        age = rootView.findViewById(R.id.editAge);
        phone = rootView.findViewById(R.id.editPhone);
        saveEdit = rootView.findViewById(R.id.save_edit);
        cancelEdit = rootView.findViewById(R.id.cancel_edit);

        saveEdit.setOnClickListener(SaveEditOnClick);
        cancelEdit.setOnClickListener(CancelEditOnClick);
        age.setMaxValue(3);
        age.setMinValue(0);
        age.setDisplayedValues(ageRange);
        KeyListener mKeyListener = Email.getKeyListener();
        Email.setKeyListener(null);

        //current is *****Coach******
        if (isCoach && !isManager) {
            currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
            //fill text view with user info
            prevPass = currentCoach.getPassword();
            fullName.setText(currentCoach.getFullName());
            Email.setText(currentCoach.getEmail());
            password.setText(currentCoach.getPassword());
            confPassword.setText(currentCoach.getPassword());
            phone.setText(currentCoach.getPhone());
            String ageString = currentCoach.getAge();
            for (int i = 0; i < ageRange.length; i++) {
                if (ageRange[i].equals(ageString))
                    age.setValue(i);
            }

        }
        //current is *****Trainee******
        if (!isCoach && !isManager) {
            currentUser = FitForLifeDataManager.getInstance().getCurrentUser();
            prevPass = currentUser.getPassword();
            Log.d(TAG, "user ?? : " + currentUser.getFullName() + prevPass);
            //fill text view with user info
            fullName.setText(currentUser.getFullName());
            Email.setText(currentUser.getEmail());
            password.setText(currentUser.getPassword());
            confPassword.setText(currentUser.getPassword());
            phone.setText(currentUser.getPhone());
            String ageString = currentUser.getAge();
            for (int i = 0; i < ageRange.length; i++) {
                if (ageRange[i].equals(ageString))
                    age.setValue(i);
            }

        }
        //current is *****Manager******
        if (!isCoach && isManager) {
            currentCoach = FitForLifeDataManager.getInstance().getCurrentManager();
            prevPass = currentCoach.getPassword();
            //fill text view with user info
            fullName.setText(currentCoach.getFullName());
            Email.setText(currentCoach.getEmail());
            password.setText(currentCoach.getPassword());
            confPassword.setText(currentCoach.getPassword());
            phone.setText(currentCoach.getPhone());
            String ageString = currentCoach.getAge();
            for (int i = 0; i < ageRange.length; i++) {
                if (ageRange[i].equals(ageString))
                    age.setValue(i);
            }

        }
        return rootView;
    }


    private View.OnClickListener SaveEditOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            newFullName = fullName.getText().toString();
            String newPassword = password.getText().toString();
            String newConPassword = confPassword.getText().toString();
            newPhone = phone.getText().toString();
            newAge = ageRange[age.getValue()];

            boolean checkError = true;

            while (checkError) {
                boolean feError = false;
                if (TextUtils.isEmpty(newFullName)) {
                    fullName.setError("full name is Required.");
                    feError = true;
                }


                if (TextUtils.isEmpty(newPassword)) {
                    password.setError("Password is Required.");
                    feError = true;

                }


                if (!newPassword.matches(newConPassword)) {
                    confPassword.setError("Password dont match!");
                    feError = true;
                }
                if (feError)
                    return;
                else
                    checkError = false;
            }

            if (isCoach || isManager) {
                //if user want to change pass
                if (!prevPass.equals(newPassword)) {
                    updatePassword(newPassword);
                    return;
                }

                updateCoach(false);


            }


            if (!isCoach && !isManager) {
                Log.d(TAG, "onClick: get password " + currentUser.getPassword() + " new paswword " + newPassword);
                if (!currentUser.getPassword().equals(newPassword)) {
                    updatePassword(newPassword);
                    return;
                }

                updateTrainee(false);


            }


        }


    };

    private void updateCoach(boolean fromUpdate) {
        currentCoach.setPhone(newPhone);
        currentCoach.setAge(newAge);
        currentCoach.setFullName(newFullName);
        if (fromUpdate) {
            // meaning password didnt update
            if (prevPass.equals(currentCoach.getPassword())) {
                password.setError("Update Password Failed");
                return;
            }
        }


        Map<String, Object> user = new HashMap<>();
        user.put("fullName", newFullName);
        user.put("age", newAge);
        user.put("phone", newPhone);
        user.put("password", currentCoach.getPassword());

        fStore.collection("users").document(currentCoach.getEmail()).set(currentCoach).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (isCoach && !isManager)
                    FitForLifeDataManager.getInstance().setCurrentCoach(currentCoach);
                else
                    FitForLifeDataManager.getInstance().setCurrentManager(currentCoach);
                FitForLifeDataManager.getInstance().updateCoach(currentCoach);

                mDatabase.child("Users").child(currentCoach.getId()).child("fullName").setValue(currentCoach.getFullName());

                Toast.makeText(getActivity(),
                        "Your Profile Details Was updated Successfully", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();

            }
        });


    }


    private void updateTrainee(boolean fromUpdate) {

        currentUser.setPhone(newPhone);
        currentUser.setAge(newAge);
        currentUser.setFullName(newFullName);
        // meaning password didnt update
        if (fromUpdate) {
            if (prevPass.equals(currentUser.getPassword())) {
                password.setError("Update Password Failed");
                return;
            }
        }

        Map<String, Object> user = new HashMap<>();
        user.put("fullName", newFullName);
        user.put("age", newAge);
        user.put("phone", newPhone);
        user.put("password", currentUser.getPassword());

        fStore.collection("users").document(currentUser.getEmail()).set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FitForLifeDataManager.getInstance().setCurrentUser(currentUser);
                FitForLifeDataManager.getInstance().updateUser(currentUser);
                mDatabase.child("Users").child(currentUser.getId()).child("fullName").setValue(currentUser.getFullName());

                Log.d(TAG, " current user is update in sql  " + currentUser);

                Toast.makeText(getActivity(),
                        "Your Profile Details Was updated Successfully", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error Updating  document", e);
            }
        });

    }

    private void updatePassword(final String newPassword) {

        Log.d(TAG, "updatePassword:BEFORE UPDATING IN FIREBISE " + currentUser.getPassword());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (!isCoach && !isManager) {
                                currentUser.setPassword(newPassword);
                                updateTrainee(true);
                            } else {
                                currentCoach.setPassword(newPassword);
                                updateCoach(true);

                            }
                            Log.d(TAG, "User password updated.");
                        } else {
                            if (!isCoach && !isManager) {
                                updateTrainee(true);
                            } else {
                                updateCoach(true);

                            }
                            Log.d(TAG, " FAILED User password updated.");
                        }
                    }
                });
    }


    private View.OnClickListener CancelEditOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
//
//            infoTab_Fragment editInfo_fragment = new infoTab_Fragment();
//            FragmentTransaction transaction= getFragmentManager().beginTransaction();
//            transaction.replace(R.id.root_frame, new EditInfo_Fragment());
//            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//            transaction.addToBackStack(null);
//            transaction.commit();


            //   getActivity().getFragmentManager().popBackStack();
            getActivity().onBackPressed();

        }
    };

}
