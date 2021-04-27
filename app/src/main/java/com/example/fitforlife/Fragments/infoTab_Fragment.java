package com.example.fitforlife.Fragments;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class infoTab_Fragment extends Fragment {

    private Activity ctx;
    private FragmentActivity myContext;
    private boolean isCoach;
    private boolean isManager;

    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    TextView fullName, Email, password, age, phone, groupLabel, groupName;
    String currentUser;
    String userID;

    public infoTab_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_info_tab_, container, false);
        ctx = getActivity();

        // Initialize Firebase Auth & FireStore
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]
        fullName = rootView.findViewById(R.id.showFullname);
        Email = rootView.findViewById(R.id.showEmail);
        password = rootView.findViewById(R.id.showPassword);
        age = rootView.findViewById(R.id.showAge);
        phone = rootView.findViewById(R.id.showPhone);
        groupLabel = rootView.findViewById(R.id.groupLabel);
        groupName = rootView.findViewById(R.id.groupName);

        //current is *****Trainee******
        if (FitForLifeDataManager.getInstance().getCurrentUser() != null && FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentManager() == null) {
            isCoach = false;
            isManager = false;
            UserInfo current=FitForLifeDataManager.getInstance().getCurrentUser();
            fullName.setText(current.getFullName());
                Email.setText(current.getEmail());
                password.setText(current.getPassword());
                age.setText(current.getAge());
                phone.setText(current.getPhone());

                if (current.getGroupId()!=null){
                    Group tmp = FitForLifeDataManager.getInstance().getGroup(current.getGroupId());
                    groupName.setText(tmp.getGroupName());

                }}

        //current is *****Coach******
        if (FitForLifeDataManager.getInstance().getCurrentUser() == null && FitForLifeDataManager.getInstance().getCurrentCoach() != null && FitForLifeDataManager.getInstance().getCurrentManager() == null) {
            CoachInfo current=FitForLifeDataManager.getInstance().getCurrentCoach();
            fullName.setText(current.getFullName());
            Email.setText(current.getEmail());
            password.setText(current.getPassword());
            age.setText(current.getAge());
            phone.setText(current.getPhone());
            groupLabel.setVisibility(View.GONE);
            groupName.setVisibility(View.GONE);
            isCoach = true;
            isManager = false;
        }


        //current is *****Manager******
        if (FitForLifeDataManager.getInstance().getCurrentUser() == null && FitForLifeDataManager.getInstance().getCurrentCoach() == null && FitForLifeDataManager.getInstance().getCurrentManager() != null) {
            CoachInfo current = FitForLifeDataManager.getInstance().getCurrentManager();
            fullName.setText(current.getFullName());
            Email.setText(current.getEmail());
            password.setText(current.getPassword());
            age.setText(current.getAge());
            phone.setText(current.getPhone());
            groupLabel.setVisibility(View.GONE);
            groupName.setVisibility(View.GONE);
            isCoach = false;
            isManager = true;
        }
//

        // on click listener for edit button
        final Button edit_btn = (Button) rootView.findViewById(R.id.editInfo);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // EditInfo_Fragment editInfo_fragment = new EditInfo_Fragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.root_frame, new EditInfo_Fragment(isCoach, isManager));
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();


            }

        });
        // Inflate the layout for this fragment
        return rootView;
    }


    ///teeestttt
    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}
