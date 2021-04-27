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

import com.example.fitforlife.Fragments.EditGroup_Fragment;
import com.example.fitforlife.Fragments.Groups_Fragment;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class GroupAdapter extends ArrayAdapter<Group> {

    private static final String TAG = "GroupAdapter";
    private List<Group> GroupTraineeList;
    private Context context;
    private boolean isManager;
    private Fragment fragment;
    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    public GroupAdapter(Context context, int resource, List<Group> data, Fragment fragment) {
        super(context, resource, data);
        this.GroupTraineeList = data;
        this.context = context;
        this.fragment = fragment;


    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_groups_trainee, null, false);

        if (FitForLifeDataManager.getInstance().getCurrentCoach() != null && FitForLifeDataManager.getInstance().getCurrentManager() == null)
            isManager = false;
        else
            isManager = true;
        final int pos = position;
        final Group currentItem = getItem(position);

        // Initialize Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // [END initialize]

        TextView groupTraineeName = rootView.findViewById(R.id.nameGroupTrainee);
        groupTraineeName.setText(currentItem.getGroupName());


        ImageView editIcon = (ImageView) rootView.findViewById(R.id.EditGroupTraineeIcon);
        ImageView deleteIcon = (ImageView) rootView.findViewById(R.id.deleteGroupTraineeIcon);
        ImageView icon = rootView.findViewById(R.id.groupTraineeIcon);
        icon.setImageResource(R.drawable.ic_group);


        // action Listener for edit icon
        editIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                EditGroup_Fragment editGroup_fragment = new EditGroup_Fragment(currentItem);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content, editGroup_fragment).addToBackStack(null).commit();


            }

        });
        // action Listener for edit icon
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final String title;
                final String msg;

                title = context.getResources().getString(R.string.deleteGroup);
                msg = context.getResources().getString(R.string.deleteGroupMsg);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (FitForLifeDataManager.getInstance().getAllGroupTrainee(currentItem).isEmpty()) {

                            fStore.collection("groups").document(currentItem.getGroupId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            CollectionReference docRef = fStore.collection("groups").document(currentItem.getGroupId()).collection("sessions");
                                            docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@com.google.firebase.database.annotations.Nullable QuerySnapshot snapshot, @com.google.firebase.database.annotations.Nullable FirebaseFirestoreException e) {

                                                    if (e != null) {
                                                        return;
                                                    }

                                                    if (snapshot != null && !snapshot.isEmpty()) {

                                                        for (DocumentSnapshot document2 : snapshot.getDocuments()) {
                                                            fStore.collection("groups").document(currentItem.getGroupId()).collection("sessions").document(document2.getId())
                                                                    .delete()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                            FitForLifeDataManager.getInstance().deleteGroup(currentItem);
                                                                            FitForLifeDataManager.getInstance().deleteGroupSessions(currentItem);
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "Error deleting document", e);
                                                                        }
                                                                    });

                                                        }

                                                    } else {
                                                        Log.d(TAG, "Current data: null");

                                                    }
                                                }
                                            });


                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            GroupTraineeList.remove(pos);
                                            GroupAdapter.this.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                        }
                                    });
                            Toast.makeText(context, context.getResources().getString(R.string.removedGroup), Toast.LENGTH_SHORT).show();

                        } else {
                           // if (isManager) {
                                ((Groups_Fragment) fragment).popUpWhenDeletingGroup(currentItem);
                           // }
                        }


                    }
                }); // end " yes "

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


}