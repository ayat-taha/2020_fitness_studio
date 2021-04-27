package com.example.fitforlife.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitforlife.Adapters.GroupAdapter;
import com.example.fitforlife.Adapters.TraineeAdapter;
import com.example.fitforlife.Adapters.TraineePopUpAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;

import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Groups_Fragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Groups_Fragment";
    private ListView groupsListView;
    private GroupAdapter adapter;
    private Context ctx;
    private boolean isManager;
    EditText inputSearch;
    TextView groupsSum;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private CoachInfo currentCoach;
    List<Group> groups;
    List<Group> coachGroups; // for list
    List<CoachInfo> coachTable; //for spinner

    public Groups_Fragment(boolean isManager) {
        // Required empty public constructor
        this.isManager = isManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        ctx = getActivity();

        // action listner add group
        ImageView buttonOne = (ImageView) rootView.findViewById(R.id.addGroup);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content, new AddGroup_Fragment(isManager));
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        });

        groupsListView = rootView.findViewById(R.id.listForGroups);
        inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);

        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Groups_Fragment.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Spinner spin = (Spinner) rootView.findViewById(R.id.coachSpinner);
        coachGroups = new ArrayList<>();  // for groups list
        coachTable = new ArrayList<>(); //for spinner (filter)

        if (isManager) {
            currentCoach = FitForLifeDataManager.getInstance().getCurrentManager();
            coachGroups = FitForLifeDataManager.getInstance().getAllManagerGroups(currentCoach);
            coachTable = FitForLifeDataManager.getInstance().getAllManagerCoaches(currentCoach); // for spinner
            coachTable.add(0, new CoachInfo("All Groups"));
            ArrayAdapter<CoachInfo> adapter = new ArrayAdapter<CoachInfo>(getContext(), R.layout.simple_spinner_item, coachTable);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(adapter);
            spin.setSelection(0); //set selection to all groups
            spin.setOnItemSelectedListener(this);
        } else { // current is coach
            spin.setVisibility(View.GONE);
            currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
            coachGroups = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
        }

        if (!coachGroups.isEmpty()) {
            adapter = new GroupAdapter(ctx, R.layout.card_groups_trainee, coachGroups, Groups_Fragment.this);
            groupsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        groupsSum = (TextView) rootView.findViewById(R.id.groupsSum);
        groupsSum.setText(coachGroups.size() + "");

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Log.i("IsRefresh", "Yes");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {

            coachGroups = FitForLifeDataManager.getInstance().getAllManagerGroups(currentCoach);
            adapter = new GroupAdapter(ctx, R.layout.card_groups_trainee, coachGroups, Groups_Fragment.this);
            groupsListView.setAdapter(adapter);
            groupsSum.setText(coachGroups.size() + "");

        } else {
            CoachInfo coach = coachTable.get(position);
            coachGroups = FitForLifeDataManager.getInstance().getAllCoachGroup(coach.getEmail());
            adapter = new GroupAdapter(ctx, R.layout.card_groups_trainee, coachGroups, Groups_Fragment.this);
            groupsListView.setAdapter(adapter);
            groupsSum.setText(coachGroups.size() + "");

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (isManager)
            coachGroups = FitForLifeDataManager.getInstance().getAllManagerGroups(FitForLifeDataManager.getInstance().getCurrentManager());
    }

    public void popUpWhenDeletingGroup(Group currentItem) {

        dialogBuilder = new AlertDialog.Builder(ctx);
        final View sessionPopUpView = ((Activity) ctx).getLayoutInflater().inflate(R.layout.delete_group_popup, null);
        dialogBuilder.setView(sessionPopUpView);
        dialog = dialogBuilder.create();

        ListView list = sessionPopUpView.findViewById(R.id.traineeList);
        List<UserInfo> trainee = FitForLifeDataManager.getInstance().getAllGroupTrainee(currentItem);

        TraineePopUpAdapter TraineeAdapter = new TraineePopUpAdapter(ctx, R.layout.card_trainee_popup, trainee, Groups_Fragment.this);
        list.setAdapter(TraineeAdapter);
        TextView close = sessionPopUpView.findViewById(R.id.txtclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setTitle(getResources().getString(R.string.delete));
        dialog.setTitle(getResources().getString(R.string.delete));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //to round popup corners
        dialog.show();


    }

    public void closeDialog() {
        dialog.dismiss();

    }
}
