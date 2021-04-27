package com.example.fitforlife.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.example.fitforlife.R;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTrainee_Fragment extends Fragment {
    private static final String TAG = "AddTrainee_Fragment";
    private FirebaseAuth fAuth;
    private FirebaseAuth mAuth2;
    FirebaseFirestore fStore;
    BottomSheetDialog btm;
    View btsv;
    GoalAdapter adapter;
    private NumberPicker agePicker;
    private Button addBtn, cancelBtn;
    String[] ageRange;
    private EditText fullName, password, email, phone;
    private Spinner groupSpinner;
    private String selectedGroup;
    private String currentUser;
    private String currentStudio;
    private boolean isManager;

    TextView weeklyGoal;
    EditText goalWeight;
    List<String> goalsOptions;
    Context context;

    public AddTrainee_Fragment(boolean isManager) {
        // Required empty public constructor
        this.isManager = isManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_add_trainee, container, false);
        context = getActivity();
        ageRange = context.getResources().getStringArray(R.array.ageRange);
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize_auth]
        currentUser = fAuth.getCurrentUser().getEmail();
        goalsOptions = new ArrayList<>();
        goalsOptions = Arrays.asList(context.getResources().getStringArray(R.array.goals));

        final DocumentReference docRef = fStore.collection("users").document(currentUser);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentStudio = documentSnapshot.getData().get("studio").toString();

            }
        });

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
        addBtn = rootView.findViewById(R.id.save_addTrainee);
        addBtn.setOnClickListener(addOnClick);

        cancelBtn = rootView.findViewById(R.id.cancel_addTrainee);
        cancelBtn.setOnClickListener(cancelOnClick);

        // Number Picker
        agePicker = rootView.findViewById(R.id.addTrainee_age);
        fullName = rootView.findViewById(R.id.addTrainee_FullName);
        email = rootView.findViewById(R.id.addTrainee_email);
        password = rootView.findViewById(R.id.addTrainee_password);
        phone = rootView.findViewById(R.id.addTrainee_phone);
        goalWeight = rootView.findViewById(R.id.goalWeight);
        goalWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        weeklyGoal = rootView.findViewById(R.id.weeklyGoal);
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

                btm.setContentView(btsv);
                btm.show();

                goalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        weeklyGoal.setText(goalsOptions.get(position));
                        adapter.SetSelectedPos(position);
                        adapter.notifyDataSetChanged();
                        btm.dismiss();
                    }
                });
            }

        });
        agePicker.setMaxValue(3);
        agePicker.setMinValue(0);
        agePicker.setDisplayedValues(ageRange);


        groupSpinner = rootView.findViewById(R.id.addTrainee_groupNameSpinner);

        ArrayList<Group> groupsForSpinner = new ArrayList<Group>();
        if (isManager)
            groupsForSpinner = (ArrayList<Group>) FitForLifeDataManager.getInstance().getAllManagerGroups(FitForLifeDataManager.getInstance().getCurrentManager());
        else

            groupsForSpinner = (ArrayList<Group>) FitForLifeDataManager.getInstance().getAllCoachGroup(currentUser);

        if (!groupsForSpinner.isEmpty()) {
            ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(context, R.layout.simple_spinner_item, groupsForSpinner);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            groupSpinner.setAdapter(adapter);
        }

        return rootView;
    }


    //onclick method for save Button > adds new trainee to firebase and sqlLite
    private View.OnClickListener addOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            final String emailString = email.getText().toString().trim();
            final String passwordString = password.getText().toString();
            final String fullNameString = fullName.getText().toString();
            final String phoneString = phone.getText().toString();
            final String ageString = ageRange[agePicker.getValue()];
            final String weightGoal = goalWeight.getText().toString();
            final String weeklyGoalString = weeklyGoal.getText().toString();

            if (!checkError(fullNameString, phoneString, ageString, emailString, passwordString, weightGoal, weeklyGoalString)) {
                mAuth2.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                            map.put("type", "Trainee");
                            map.put("status", "offline");
                            map.put("search", fullNameString.toLowerCase());
                            map.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/instagramtest-fcbef.appspot.com/o/placeholder.png?alt=media&token=b09b809d-a5f8-499b-9563-5252262e9a49");
                            reference.setValue(map);

                            Log.d(TAG, "onComplete: user is now created &  " + mAuth2.getCurrentUser().getEmail());

                            //Date added
                            Calendar dateAdded = Calendar.getInstance();
                            dateAdded.set(Calendar.HOUR_OF_DAY, 00);
                            dateAdded.set(Calendar.MINUTE, 01);
                            dateAdded.set(Calendar.SECOND, 00);
                            dateAdded.set(Calendar.MILLISECOND, 00);

                            DocumentReference documentReference = fStore.collection("users").document(emailString);
                            final UserInfo newUser;
                            if (selectedGroup != null)
                                newUser = new UserInfo(mAuth2.getCurrentUser().getUid(), fullNameString, ageString, phoneString, emailString, passwordString, dateAdded.getTimeInMillis(), "user", currentStudio, selectedGroup, Double.parseDouble(goalWeight.getText().toString()), weeklyGoal.getText().toString());
                            else
                                newUser = new UserInfo(mAuth2.getCurrentUser().getUid(), fullNameString, ageString, phoneString, emailString, passwordString, dateAdded.getTimeInMillis(), "user", currentStudio, Double.parseDouble(goalWeight.getText().toString()), weeklyGoal.getText().toString());
                            documentReference.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + newUser.toString());
                                    FitForLifeDataManager.getInstance().createUser(newUser);
                                    if (isManager)
                                        ManagerCoachTraineeGroupsActivity.SetFragment(false, true, false);
                                    else
                                        GroupsTraineeActivity.SetFragment(true);

                                    getActivity().onBackPressed();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
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
            if (isManager)
                ManagerCoachTraineeGroupsActivity.SetFragment(false, true, false);
            else
                GroupsTraineeActivity.SetFragment(true);

            getActivity().onBackPressed();
        }
    };

    private Boolean checkError(String fullNameString, String phoneString, String ageString, String emailString, String passwordString, String weightGoal, String weeklyGoalString) {
        boolean checkError = true;



        while (checkError) {

            boolean feError = false;

            if (groupSpinner == null || groupSpinner.getSelectedItem() == null) {
                Toast.makeText(context, "You Must Chose Group!", Toast.LENGTH_LONG).show();
                feError = true;
            } else {
                if (checkGroupAvailability()) {
                    Toast.makeText(context, "Choosen Group is full , Choose another!", Toast.LENGTH_LONG).show();
                    feError = true;
                }
            }


            if (TextUtils.isEmpty(weightGoal)) {
                goalWeight.setError("Weight Goal is Required.");
                feError = true;

            }
            if (TextUtils.isEmpty(weeklyGoalString)) {
                weeklyGoal.setError("weekly Goal is Required.");
                feError = true;
            }
            if (TextUtils.isEmpty(fullNameString)) {
                fullName.setError("full name is Required.");
                feError = true;
            }

            if (TextUtils.isEmpty(emailString)) {
                email.setError("Email is Required.");
                feError = true;

            }

            if (TextUtils.isEmpty(passwordString)) {
                password.setError("Password is Required.");
                feError = true;

            }

            if (TextUtils.isEmpty(phoneString)) {
                phone.setError("Password is Required.");
                feError = true;

            }
            if (passwordString.length() < 6) {
                password.setError("Password Must be >= 6 Characters");
                feError = true;

            }


            if (feError)
                return true;
            else
                checkError = false;
        }
        return false;
    }

    private boolean checkGroupAvailability() {
        Group group = (Group) groupSpinner.getSelectedItem();
        int numberOfTraineeInGroup = FitForLifeDataManager.getInstance().getAllGroupTrainee(group).size();
        if (numberOfTraineeInGroup + 1 > Integer.valueOf(group.getNumberOfTrainee()))
            return true;

        else {
            selectedGroup = ((Group) groupSpinner.getSelectedItem()).getGroupId();
            return false;
        }


    }


} //end Fragment