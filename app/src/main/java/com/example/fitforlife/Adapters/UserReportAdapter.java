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
import com.example.fitforlife.Model.User;
import com.example.fitforlife.Model.UserInfo;
import com.example.fitforlife.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class UserReportAdapter extends ArrayAdapter<UserInfo> {


    private Context context;
    private boolean isPaymentReport;

    public UserReportAdapter(Context context, int resource, List<UserInfo> data, boolean isPaymentReport) {
        super(context, resource, data);
        this.context = context;
        this.isPaymentReport = isPaymentReport;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_trainee_report, null, false);


        final UserInfo currentItem = getItem(position);

        TextView traineeName = rootView.findViewById(R.id.nameTraineeReport);
        ImageView userImage = rootView.findViewById(R.id.TraineeIconReport);
        ImageView notPaidIc = rootView.findViewById(R.id.not_paid_ic);
        if (isPaymentReport)
            notPaidIc.setVisibility(View.VISIBLE);
        traineeName.setText(currentItem.getFullName());
        getUserInfo(userImage, traineeName, currentItem.getId());
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