package com.example.fitforlife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fitforlife.R;

import java.util.List;

public class AttendanceAdapter extends ArrayAdapter<String> {


        private List<String> traineeList;
        private Context context;

        private boolean isGroup;
        private boolean isProgress;


        public AttendanceAdapter(Context context, int resource, List<String> data ) {
            super(context, resource, data);
            this.traineeList = data;
            this.context = context;

        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View rootView = inflater.inflate(R.layout.card_attendence, null, false);


           // final int pos = position;
            final String currentItem = getItem(position);

            TextView TraineeName = rootView.findViewById(R.id.AttendedName);
            TraineeName.setText(currentItem);

            return rootView;
        }
}
