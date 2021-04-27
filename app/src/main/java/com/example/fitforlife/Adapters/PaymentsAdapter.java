package com.example.fitforlife.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.fitforlife.Activities.PaymentsActivity;
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;;


public class PaymentsAdapter extends ArrayAdapter<UserInfo> {


    private Context context;


    public PaymentsAdapter(Context context, int resource, List<UserInfo> data) {
        super(context, resource, data);
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_trainee_payments, null, false);


        final UserInfo currentItem = getItem(position);

        TextView traineeName = rootView.findViewById(R.id.nameTrainee);
        ImageView userImage = rootView.findViewById(R.id.TraineeIcon);
        ImageView editTraineePayments = (ImageView) rootView.findViewById(R.id.editPayments);
        traineeName.setText(currentItem.getFullName());
        getUserInfo(userImage, traineeName,currentItem.getId());

        // action Listener for edit icon
        editTraineePayments.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent paymentsActivity = new Intent(context, PaymentsActivity.class);
                paymentsActivity.addFlags(FLAG_ACTIVITY_NEW_TASK);
                paymentsActivity.putExtra("currentUser", currentItem);
                Log.d("userTrans", "added  user in put extra : " + currentItem.toString());
                getContext().startActivity(paymentsActivity);
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