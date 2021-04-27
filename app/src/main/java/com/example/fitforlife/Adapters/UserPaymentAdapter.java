package com.example.fitforlife.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.LinkedHashMap;


public class UserPaymentAdapter extends BaseAdapter implements Filterable {


    private Context context;
    private LinkedHashMap<UserInfo, Integer> mData;
    private LinkedHashMap<UserInfo, Integer> temporaryData;
    private UserInfo[] mKeys;


    public UserPaymentAdapter(Context context, LinkedHashMap<UserInfo, Integer> data) {
        this.context = context;
        mData = data;
        temporaryData = data;
        mKeys = temporaryData.keySet().toArray(new UserInfo[data.size()]);

    }


    @Override
    public int getCount() {
        return temporaryData.size();
    }

    @Override
    public Integer getItem(int position) {
        return temporaryData.get(mKeys[position]);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.card_trainee_monthly_report, null, false);


        UserInfo user = mKeys[position];
        Integer amount = getItem(position);

        TextView traineeName = rootView.findViewById(R.id.nameTraineeReport);
        TextView amountTXT = rootView.findViewById(R.id.paymentAmount);
        ImageView userImage = rootView.findViewById(R.id.TraineeIconReport);
        amountTXT.setText(amount+" ");
        getUserInfo(userImage, traineeName, user.getId());
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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                temporaryData = (LinkedHashMap<UserInfo, Integer>) results.values;
                Log.d("adapterPaymentFilter", results.values.toString());
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                LinkedHashMap<UserInfo, Integer> FilteredList = new LinkedHashMap<UserInfo, Integer>();
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = mData;
                    results.count = mData.size();
                } else {
                    for (int i = 0; i < mKeys.length; i++) {
                        String data = mKeys[i].getFullName();
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredList.put(mKeys[i], mData.get(mKeys[i]));
                        }
                    }
                    results.values = FilteredList;
                    results.count = FilteredList.size();
                }
                return results;
            }
        };
        return filter;
    }
}