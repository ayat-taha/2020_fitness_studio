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
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Fragments.EditTrainee_Fragment;
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

public class UserAgeReportAdapter extends ArrayAdapter<UserInfo> {


    private Context context;

    public UserAgeReportAdapter(Context context, int resource, List<UserInfo> data) {
        super(context, resource, data);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_trainee_age_report, null, false);


        final UserInfo currentItem = getItem(position);

        TextView traineeName = rootView.findViewById(R.id.nameTraineeReport);
        TextView traineeAge = rootView.findViewById(R.id.ageTraineeReport);
        ImageView userImage = rootView.findViewById(R.id.TraineeIconReport);
        ImageView editUser = rootView.findViewById(R.id.editUser);
        traineeAge.setText(currentItem.getAge());
        getUserInfo(userImage, traineeName, currentItem.getId());

        // action Listener for edit icon
        editUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                EditTrainee_Fragment editTrainee_fragment = new EditTrainee_Fragment(currentItem);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content, editTrainee_fragment).addToBackStack(null).commit();
            }

        });

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


} // end adapter
