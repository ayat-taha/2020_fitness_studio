package com.example.fitforlife.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Fragments.EditTrainee_Fragment;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TraineeAdapter extends ArrayAdapter<UserInfo> {

    private static final String TAG = "TraineeAdapter";
    FirebaseFirestore fStore;
    private List<UserInfo> GroupTraineeList;
    private Context context;
    private FirebaseAuth fAuth;


    public TraineeAdapter(Context context, int resource, List<UserInfo> data) {
        super(context, resource, data);
        this.GroupTraineeList = data;
        this.context = context;


    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_trainee_fragment, null, false);

// Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        final int pos = position;
        final UserInfo currentItem = getItem(position);
        ImageView userImage = rootView.findViewById(R.id.icon);
        TextView groupTraineeName = rootView.findViewById(R.id.nameCoachOrTrainee);
        groupTraineeName.setText(currentItem.getFullName());

        getUserInfo(userImage, groupTraineeName, currentItem.getId());


        ImageView editIcon = (ImageView) rootView.findViewById(R.id.EditGroupTraineeIcon);
        ImageView deleteIcon = (ImageView) rootView.findViewById(R.id.deleteGroupTraineeIcon);


        // action Listener for edit icon
        editIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                EditTrainee_Fragment editTrainee_fragment = new EditTrainee_Fragment(currentItem);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content, editTrainee_fragment).addToBackStack(null).commit();


            }

        });
        // action Listener for edit icon
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String title;
                final String msg;

                title = context.getResources().getString(R.string.deleteTrainee);
                msg = context.getResources().getString(R.string.deleteTraineeMsg);


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                        currentItem.setStudio(null);
                        currentItem.setGroupId(null);
                        // TODO: 12/15/2020 remove from user/group firebase and sqllite
                        final DocumentReference documentReference = fStore.collection("users").document(currentItem.getEmail());
                        documentReference.set(currentItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: update group " + documentReference.getId());
                                FitForLifeDataManager.getInstance().updateUser(currentItem);
                                Log.d(TAG, "current user set password ? " + currentItem.getStudio());
                                GroupTraineeList.remove(pos);
                                TraineeAdapter.this.notifyDataSetChanged();
                                FitForLifeDataManager.getInstance().deleteUser(currentItem);
                                Toast.makeText(context, context.getResources().getString(R.string.removedTraineeSucc), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                                Toast.makeText(context, context.getResources().getString(R.string.removedTraineeFail), Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                });

                builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

        });


        return rootView;


    }

    private void getUserInfo(final ImageView imageView, final TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);
        Log.d(TAG, "user id : " + userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "user name : " + user.getFullName());
                Glide.with(context).load(user.getImageUrl()).into(imageView);
                username.setText(user.getFullName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}