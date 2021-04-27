package com.example.fitforlife.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.example.fitforlife.Adapters.GoalAdapter;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GoalTab_fragment extends Fragment {
    private static final String TAG = "EditTrainee_Fragment";

    private Activity ctx = null;
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private Context context;
    BottomSheetDialog btm;
    View btsv;
    GoalAdapter adapter;
    TextView weeklyGoal;
    EditText goalWeight;
    List<String> goalsOptions;

    private UserInfo currentUser;
    private Button saveGoal;

    public GoalTab_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ctx = getActivity();
        context = getActivity();
        final View rootView = inflater.inflate(R.layout.fragment_goal_tab, container, false);



        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        saveGoal = rootView.findViewById(R.id.saveGoal);
        saveGoal.setOnClickListener(saveOnClick);

        goalWeight = rootView.findViewById(R.id.goalWeight);
        goalWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        weeklyGoal = rootView.findViewById(R.id.weeklyGoal);
        currentUser = FitForLifeDataManager.getInstance().getCurrentUser();
        Log.d(TAG, "onCreateView: Current user " + currentUser);

        goalsOptions = new ArrayList<>();
        goalsOptions = Arrays.asList(context.getResources().getStringArray(R.array.goals));

        String tmp;

        if(currentUser!=null){

        if (currentUser.getWeightGoal() != null) {
            goalWeight.setText("" + currentUser.getWeightGoal());
        }
        if (currentUser.getWeeklyGoal() != null)
            weeklyGoal.setText(currentUser.getWeeklyGoal());
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

                if (currentUser.getWeeklyGoal() != null) {
                    adapter.SetSelectedPos(goalsOptions.indexOf(currentUser.getWeeklyGoal()));
                }
                btm.setContentView(btsv);
                btm.show();

                goalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        currentUser.setWeeklyGoal(goalsOptions.get(position));
                        weeklyGoal.setText(goalsOptions.get(position));
                        adapter.SetSelectedPos(position);
                        adapter.notifyDataSetChanged();
                        addWeightNotification(FitForLifeDataManager.getInstance().getCurrentUser(),goalsOptions.get(position));
                        btm.dismiss();
                    }
                });
            }

        });

        }
        return rootView;
    }


    private View.OnClickListener saveOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if(FitForLifeDataManager.getInstance().getCurrentUser()!=null){
            if (!checkError()) {

                currentUser.setWeightGoal(Double.parseDouble(goalWeight.getText().toString()));
                final DocumentReference documentReference = fStore.collection("users").document(currentUser.getEmail());
                documentReference.set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: update User Goal " + documentReference.getId());
                        FitForLifeDataManager.getInstance().updateUserGoal(currentUser);
                        FitForLifeDataManager.getInstance().setCurrentUser(currentUser);
                        addWeightNotification(FitForLifeDataManager.getInstance().getCurrentUser(),goalWeight.getText().toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });

            }

}


        }
    };

    private boolean checkError() {
        String w = goalWeight.getText().toString();
        Boolean error = false;
        if (TextUtils.isEmpty(w)) {
            goalWeight.setError(getResources().getString(R.string.setGoal_msg));
            error = true;
        }

        String w2 = weeklyGoal.getText().toString();
        if (TextUtils.isEmpty(w2)) {
            weeklyGoal.setError(getResources().getString(R.string.setWeeklyGoal_msg));
            error = true;
        }

        return error;
    }

    private void addWeightNotification(UserInfo currentUser,String goal) {
        Group userGroup = FitForLifeDataManager.getInstance().getGroup(currentUser.getGroupId());
        CoachInfo userCoach = FitForLifeDataManager.getInstance().getGroupCoach(userGroup);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userCoach.getId());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", currentUser.getId());
        hashMap.put("text", "updated weight goal to");
        hashMap.put("text2", "Group : " + userGroup.getGroupName());
        hashMap.put("postid", goal );
        hashMap.put("isEventRes", false);
        hashMap.put("isEventCanc", false);
        hashMap.put("isEventNotGoing", false);
        hashMap.put("isEventGoing", false);
        hashMap.put("ispost", false);
        hashMap.put("isProgress", true);
        reference.push().setValue(hashMap);
    }

}
