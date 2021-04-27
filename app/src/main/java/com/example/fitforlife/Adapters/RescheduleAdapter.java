package com.example.fitforlife.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fitforlife.Activities.CalendarRescheduleActivity;
import com.example.fitforlife.Model.Group;
import com.example.fitforlife.Model.Session;
import com.example.fitforlife.R;
import com.example.fitforlife.SQLite.FitForLifeDataManager;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RescheduleAdapter extends ArrayAdapter<Event> {
    private Context mContext;
    private int mRespurse;

    public RescheduleAdapter(@NonNull Context context, int resource, @NonNull List<Event> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mRespurse = resource;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mRespurse, parent, false);
        final Event currentItem = getItem(position);
        FitForLifeDataManager.getInstance().setCurrentCanceledEvent(currentItem);

        TextView date = convertView.findViewById(R.id.rescheduleSession);
        TextView groupName = convertView.findViewById(R.id.groupName);
        Button res = convertView.findViewById(R.id.rescheduleSessionButton);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentItem.getTimeInMillis());

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        Date currentDate = new Date(currentItem.getTimeInMillis());

        DateFormat df = new SimpleDateFormat("dd/M/yyyy hh:mm");

        date.setText("" + (df.format(currentDate)));
        Group group = FitForLifeDataManager.getInstance().getGroup(((Session) currentItem.getData()).getGroupId());
        groupName.setText(group.getGroupName());

        res.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent progressActivity = new Intent(mContext, CalendarRescheduleActivity.class);
                progressActivity.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(progressActivity);
                ;
            }
        });


        return convertView;
    }


}
