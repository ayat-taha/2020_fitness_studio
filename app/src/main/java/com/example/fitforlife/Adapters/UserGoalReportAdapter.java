package com.example.fitforlife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Model.Measurement;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserGoalReportAdapter extends ArrayAdapter<UserInfo> {


    private Context context;
    private int countReachedGoal = 0;


    public UserGoalReportAdapter(Context context, int resource, List<UserInfo> data) {
        super(context, resource, data);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_trainee_goal_report, null, false);


        final UserInfo currentItem = getItem(position);

        TextView traineeName = rootView.findViewById(R.id.nameTraineeReport);
        ImageView userImage = rootView.findViewById(R.id.TraineeIconReport);
        TextView toReachGoal = rootView.findViewById(R.id.nameTraineeReportGoal);
        TextView label = rootView.findViewById(R.id.label);
        getUserInfo(userImage, traineeName, currentItem.getId());

        Double weightGoal = currentItem.getWeightGoal();
        if (weightGoal == null) {
            toReachGoal.setText(context.getResources().getString(R.string.noWeightGoalSet));
            label.setVisibility(View.GONE);

        } else {
            List<Measurement> weight = FitForLifeDataManager.getInstance().getTraineeMeasurement(currentItem.getEmail());
            if (!weight.isEmpty()) {
                Double weigh2 = weight.get(weight.size() - 1).getWeight();
                Double kgLeftToLose = weigh2 - weightGoal;
                if (kgLeftToLose > 0) {
                    toReachGoal.setText(String.valueOf(kgLeftToLose));
                }
                if (kgLeftToLose <= 0) {
                    toReachGoal.setText(context.getResources().getString(R.string.userReachedGoal));
                    label.setVisibility(View.GONE);
                    countReachedGoal++;
                }
            } else {
                toReachGoal.setText(context.getResources().getString(R.string.NoProgressSet));
                label.setVisibility(View.GONE);
            }
        }
        return rootView;
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageUrl()).into(imageView);
                username.setText(user.getFullName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int getCountReachedGoal() {
        return countReachedGoal;
    }

    public void setCountReachedGoal(int countReachedGoal) {
        this.countReachedGoal = countReachedGoal;
    }
} // end adapter
