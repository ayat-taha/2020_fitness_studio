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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Fragments.EditGroup_Fragment;
import com.example.fitforlife.Fragments.EditTrainee_Fragment;
import com.example.fitforlife.Fragments.Groups_Fragment;
import com.example.fitforlife.Fragments.Manager_CoachFragmenet;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GroupPopUpAdapter extends ArrayAdapter<Group> {


    private List<Group> GroupList;
    private Context context;
    private Fragment fragment;


    public GroupPopUpAdapter(Context context, int resource, List<Group> data, Fragment fragment) {
        super(context, resource, data);
        this.GroupList = data;
        this.context = context;
        this.fragment = fragment;


    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_group_popup, null, false);


        final int pos = position;
        final Group currentItem = getItem(position);

        TextView groupName = rootView.findViewById(R.id.nameCoachOrTrainee);
        groupName.setText(currentItem.getGroupName());


        ImageView editIcon = (ImageView) rootView.findViewById(R.id.EditGroupIcon);


        // action Listener for edit icon
        editIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((Manager_CoachFragmenet) fragment).closeDialog();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                EditGroup_Fragment editTrainee_fragment = new EditGroup_Fragment(currentItem);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content, editTrainee_fragment).addToBackStack(null).commit();


            }

        });


        return rootView;


    }


}