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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Fragments.Manager_CoachFragmenet;
import com.example.fitforlife.Fragments.Manager_EditCoachFragment;
import com.example.fitforlife.Model.CoachInfo;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CoachAdapter extends ArrayAdapter<CoachInfo> {
    private static final String TAG = "CoachAdapter";
    FirebaseFirestore fStore;
    private List<CoachInfo> CoachesList;
    private Context context;
    private Fragment fragment;


    public CoachAdapter(Context context, int resource, List<CoachInfo> data, Fragment fragment) {
        super(context, resource, data);
        this.CoachesList = data;
        this.context = context;
        this.fragment = fragment;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_trainee_fragment, null, false);

// Initialize Firebase Auth & FireStore
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        final int pos = position;
        final CoachInfo currentItem = getItem(position);
        ImageView userImage = rootView.findViewById(R.id.icon);
        TextView groupTraineeName = rootView.findViewById(R.id.nameCoachOrTrainee);
        groupTraineeName.setText(currentItem.getFullName());
        getUserInfo(userImage, groupTraineeName, currentItem.getId());


        ImageView editIcon = (ImageView) rootView.findViewById(R.id.EditGroupTraineeIcon);
        ImageView deleteIcon = (ImageView) rootView.findViewById(R.id.deleteGroupTraineeIcon);
        ImageView icon = rootView.findViewById(R.id.groupTraineeIcon);



        // action Listener for edit icon
        editIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Manager_EditCoachFragment editCoach_fragment = new Manager_EditCoachFragment(currentItem);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content, editCoach_fragment).addToBackStack(null).commit();


            }

        });

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    final String title;
                    final String msg;

                title = context.getResources().getString(R.string.deleteCoach);
                msg = context.getResources().getString(R.string.deleteCoachMsg);


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(title);
                    builder.setMessage(msg);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            if (checkIfCoachHaveGroups(currentItem))
                                return;

                            currentItem.setStudio(null);
                            // TODO: 12/15/2020 remove from user/group firebase and sqllite
                            final DocumentReference documentReference = fStore.collection("users").document(currentItem.getEmail());
                            documentReference.set(currentItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: update group " + documentReference.getId());
                                    Log.d(TAG, "current user set password ? " + currentItem.getStudio());
                                    CoachesList.remove(pos);
                                    CoachAdapter.this.notifyDataSetChanged();
                                    FitForLifeDataManager.getInstance().deleteCoach(currentItem);
                                    Toast.makeText(context, context.getResources().getString(R.string.removedCoachSucc), Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                    Toast.makeText(context, context.getResources().getString(R.string.removedCoachFail), Toast.LENGTH_SHORT).show();

                                }
                            });
                            // TODO: 12/15/2020 remove from Coach firebase and sqllite

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                }

        });


        return rootView;


    }

    private boolean checkIfCoachHaveGroups(CoachInfo currentItem) {
        if (FitForLifeDataManager.getInstance().getAllCoachGroup(currentItem.getEmail()).isEmpty())
            return false;
        else {
            ((Manager_CoachFragmenet) fragment).popUpWhenDeleteingGroup(currentItem);
            return true;
        }
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
