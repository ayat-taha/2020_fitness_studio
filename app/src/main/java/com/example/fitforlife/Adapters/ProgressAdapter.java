package com.example.fitforlife.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Activities.AddProgressActivity;
import com.example.fitforlife.Activities.ProgressActivity;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.fitforlife.SQLite.FitForLifeDataManager;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProgressAdapter extends ArrayAdapter<UserInfo> {


    private List<UserInfo> GroupTraineeList;
    private Context context;
    private boolean isManager;


    public ProgressAdapter(Context context, int resource, List<UserInfo> data) {
        super(context, resource, data);
        this.GroupTraineeList = data;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_trainee_progress, null, false);


        final int pos = position;
        final UserInfo currentItem = getItem(position);
        TextView traineeName = rootView.findViewById(R.id.nameGroupTrainee);
        traineeName.setText(currentItem.getFullName());


        ImageView addProgress = (ImageView) rootView.findViewById(R.id.addProgress);
        ImageView showTraineeProgress = (ImageView) rootView.findViewById(R.id.showTraineeProgress);
        ImageView userImage = rootView.findViewById(R.id.TraineeIcon);
        getUserInfo(userImage, traineeName, currentItem.getId());

        if (FitForLifeDataManager.getInstance().getCurrentManager() != null)
            addProgress.setVisibility(View.GONE);

        addProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FitForLifeDataManager.getInstance().setCurrentUserProgress(currentItem);

                Intent addProgressActivity = new Intent(context, AddProgressActivity.class);
                addProgressActivity.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(addProgressActivity);

            }
        });


        // action Listener for edit icon
        showTraineeProgress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FitForLifeDataManager.getInstance().setCurrentUserProgress(currentItem);
                Intent progressActivity = new Intent(context, ProgressActivity.class);
                    progressActivity.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(progressActivity);

//                else {
//                    final String title;
//                    final String msg;
//                    if (isGroup) {
//                        title = "Delete Group";
//                        msg = "Are you sure You want to Delete this Group ?";
//                    } else {
//                        title = "Delete User";
//                        msg = "Are you sure You to Delete this User  ?";
//                    }
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle(title);
//                    builder.setMessage(msg);
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//
//                            GroupTraineeList.remove(pos);
//                            ProgressAdapter.this.notifyDataSetChanged();
//                            Toast.makeText(context, "Removed Group From Group List Successfully", Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//
//                        }
//                    });
//
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//
//
//                }
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
}