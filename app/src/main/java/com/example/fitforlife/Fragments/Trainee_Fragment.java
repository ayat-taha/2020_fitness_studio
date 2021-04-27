package com.example.fitforlife.Fragments;


import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fitforlife.Adapters.TraineeAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Trainee_Fragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "Trainee_Fragment";

    private ListView traineeListView;
    private TraineeAdapter adapter;
    List<Group> groups;
    private Context ctx;
    private CoachInfo currentCoach;
    ArrayList<String> groupsNames;
    String[] groupNamesArr;
    View rootView;
    EditText inputSearch;

    LinearLayout sumAvailableLayout;
    private boolean isManager;
    List<UserInfo> coachTrainee;
    List<Group> groupsTable;
    TextView traineesSum, availableSum;


    public Trainee_Fragment(boolean isManager) {
        // Required empty public constructor
        this.isManager = isManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_trainee, container, false);
        ctx = getActivity();


        ImageView addTrainee = (ImageView) rootView.findViewById(R.id.addTrainee);
        addTrainee.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content, new AddTrainee_Fragment(isManager));
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        traineeListView = rootView.findViewById(R.id.listForTrainee);

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
                Trainee_Fragment.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Spinner spin = (Spinner) rootView.findViewById(R.id.groupSpinner);
        // check if current logged in ismanager or coach

        coachTrainee = new ArrayList<>();  // for trainee list
        groupsTable = new ArrayList<>(); //for spinner (filter_

        if (isManager) {
            currentCoach = FitForLifeDataManager.getInstance().getCurrentManager();
            coachTrainee = FitForLifeDataManager.getInstance().getAllManagerTrainee(currentCoach);
            groupsTable = FitForLifeDataManager.getInstance().getAllManagerGroups(currentCoach);
        } else { // current is coach
            currentCoach = FitForLifeDataManager.getInstance().getCurrentCoach();
            coachTrainee = FitForLifeDataManager.getInstance().getAllCoachTrainee(currentCoach);
            groupsTable = FitForLifeDataManager.getInstance().getAllCoachGroup(currentCoach.getEmail());
            Log.d(TAG, "onCreateView: coach Trainee" + coachTrainee);
        }
        groups = new ArrayList<>();
        if (!coachTrainee.isEmpty()) {
            adapter = new TraineeAdapter(ctx, R.layout.card_trainee_fragment, coachTrainee);
            traineeListView.setAdapter(adapter);
        }


        Group tmp = new Group();
        tmp.setGroupName(this.getResources().getString(R.string.allTrainee));

        groupsTable.add(0, tmp);
        ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(getContext(), R.layout.simple_spinner_item, groupsTable);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setSelection(0); //set selection to all groups
        spin.setOnItemSelectedListener(this);

        traineesSum = (TextView) rootView.findViewById(R.id.TraineesSum);
        sumAvailableLayout = (LinearLayout) rootView.findViewById(R.id.sumAvailableLayout);
        availableSum = (TextView) rootView.findViewById(R.id.availableSum);
        return rootView;

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        Group selectedGroup = groupsTable.get(position);
        if (position == 0) {
            sumAvailableLayout.setVisibility(View.GONE);
            if (!coachTrainee.isEmpty()) {
                adapter = new TraineeAdapter(ctx, R.layout.card_trainee_fragment, coachTrainee);
                traineeListView.setAdapter(adapter);
                traineesSum.setText(coachTrainee.size() + "");
            }

        } else {
            List<UserInfo> groupCoachTrainee = new ArrayList<UserInfo>();
            Log.d(TAG, "group Trainee " + selectedGroup.getGroupId());

            groupCoachTrainee = FitForLifeDataManager.getInstance().getAllGroupTrainee(selectedGroup);
            Log.d(TAG, "group Trainee " + groupCoachTrainee);
            adapter = new TraineeAdapter(ctx, R.layout.card_trainee_fragment, groupCoachTrainee);
            traineeListView.setAdapter(adapter);
            traineesSum.setText(groupCoachTrainee.size() + "");
            sumAvailableLayout.setVisibility(View.VISIBLE);
            Log.d("traineefragmentsum", Integer.parseInt(selectedGroup.getNumberOfTrainee()) - groupCoachTrainee.size() + "");
            availableSum.setText(Integer.parseInt(selectedGroup.getNumberOfTrainee()) - groupCoachTrainee.size() + "");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
