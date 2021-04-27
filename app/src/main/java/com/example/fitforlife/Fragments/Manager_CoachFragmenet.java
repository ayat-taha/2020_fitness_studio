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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fitforlife.Activities.TraineePaymentsActivity;
import com.example.fitforlife.Adapters.CoachAdapter;
import com.example.fitforlife.Adapters.GroupAdapter;
import com.example.fitforlife.Adapters.GroupPopUpAdapter;
import com.example.fitforlife.Adapters.TraineePopUpAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Manager_CoachFragmenet extends Fragment {

    private static final String TAG = "Manager_CoachFragmenet";
    private ListView coachesListView;
    private Context ctx;
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    List<CoachInfo> coaches;
    private CoachAdapter adapter;
    private ImageView add;
    EditText inputSearch;
    TextView coachesSum;


    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;


    public Manager_CoachFragmenet() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_manager__coach_fragmenet, container, false);

        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]
        ctx = getActivity();
        coachesListView = rootView.findViewById(R.id.listForCoaches);
        coaches= FitForLifeDataManager.getInstance().getAllManagerCoaches(FitForLifeDataManager.getInstance().getCurrentManager());

        adapter = new CoachAdapter(ctx, R.layout.card_trainee_fragment, coaches, Manager_CoachFragmenet.this);
        coachesListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
                Manager_CoachFragmenet.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // action listner add group
        ImageView add = (ImageView) rootView.findViewById(R.id.addCoach);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content, new Manager_AddCoach_Fragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            } });


        coachesSum = (TextView) rootView.findViewById(R.id.groupsSum);
        coachesSum.setText(coaches.size() + "");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        coaches = FitForLifeDataManager.getInstance().getAllManagerCoaches(FitForLifeDataManager.getInstance().getCurrentManager());
        adapter = new CoachAdapter(ctx, R.layout.card_trainee_fragment, coaches, Manager_CoachFragmenet.this);
        coachesListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void popUpWhenDeleteingGroup(CoachInfo currentItem) {
        dialogBuilder = new AlertDialog.Builder(ctx);
        final View sessionPopUpView = ((Activity) ctx).getLayoutInflater().inflate(R.layout.delete_group_popup, null);
        TextView title = sessionPopUpView.findViewById(R.id.title);
        TextView subtitle = sessionPopUpView.findViewById(R.id.subTitle);
        title.setText(getResources().getString(R.string.errorDeletingCoach));
        subtitle.setText(getResources().getString(R.string.changeGroupCoach));
        dialogBuilder.setView(sessionPopUpView);
        dialog = dialogBuilder.create();

        ListView list = sessionPopUpView.findViewById(R.id.traineeList);
        List<Group> groups = FitForLifeDataManager.getInstance().getAllCoachGroup(currentItem.getEmail());

        GroupPopUpAdapter TraineeAdapter = new GroupPopUpAdapter(ctx, R.layout.card_trainee_popup, groups, Manager_CoachFragmenet.this);
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
